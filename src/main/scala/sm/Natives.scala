package sm

import imm.{Access, Type}
import virt.Obj
import java.io.{DataInputStream}


object Natives{
  val default = new DefaultNatives {}
  type NativeMap = Map[(String, Type.Desc), VmThread => Seq[virt.Val] => virt.Val]
  type NativeSeq = Seq[((String, Type.Desc), VmThread => Seq[virt.Val] => virt.Val)]
}
trait Natives{
  val properties: Map[String, String]
  val trapped: Natives.NativeMap
  val fileLoader: String => Option[Array[Byte]]
}
object NativeUtils{
  def value(x: virt.Val) = (vm: VmThread)  => x
  def value1(x: virt.Val) = (vm: VmThread) => (a: virt.Val) => x
  def value2(x: virt.Val) = (vm: VmThread) => (a: virt.Val, b: virt.Val) => x
  val noOp = value(())
  val noOp1 = value1(())
  val noOp2 = value2(())

  implicit class pimpedRoute(val m: Seq[(String, Any)]) extends AnyVal{
    def toRoute(parts: List[String] = Nil): Natives.NativeSeq = {
      m.flatMap{ case (k, v) =>
        v match{
          case thing: Seq[(String, Any)] =>
            thing.toRoute(k :: parts).map {
              case ((path, desc), func) => ((k + "/" + path, desc), func)
            }

          case func: (VmThread => Any) =>
            val (name, descString) = k.splitAt(k.indexOf('('))
            val p = parts.reverse

            val fullDescString =
              descString
                .replaceAllLiterally("L//", s"L${p(0)}/${p(1)}/")
                .replaceAllLiterally("L/", s"L${p(0)}/")

            val desc = Type.Desc.read(fullDescString)

            type V = virt.Val
            val newFunc = (vt: VmThread) => (args: Seq[virt.Val]) => func(vt) match{
              case f: ((V, V, V, V, V) => V) => f(args(0), args(1), args(2), args(3), args(4))
              case f: ((V, V, V, V) => V) => f(args(0), args(1), args(2), args(3))
              case f: ((V, V, V) => V) => f(args(0), args(1), args(2))
              case f: ((V, V) => V) => f(args(0), args(1))
              case f: (V => V) => f(args(0))
              case f: V => f
            }
            Seq((name, desc) -> newFunc)
        }
      }
    }
  }
  implicit class pimpedMap(val s: String) extends AnyVal{
    def /(a: (String, Any)*) = s -> a
    def -(a: VmThread => Any) = s -> a
  }
}
trait DefaultNatives extends Natives{

  import NativeUtils._
  val fileLoader = (name: String) => {
    val slashName = s"/$name"

    val loaded = getClass.getResourceAsStream(slashName)
    if (loaded == null) None
    else {
      val stream = new DataInputStream(loaded)
      val bytes = new Array[Byte](stream.available())
      stream.readFully(bytes)
      //imm.Util.printClass(bytes)
      Some(bytes)
    }
  }

  val properties = Map[String, String](
    /*"java.home" -> "C ->/java_home",
    "sun.boot.class.path" -> "C ->/classes",
    "file.encoding" ->"US_ASCII",
    "java.ext.dirs" -> "",
    "java.vendor" ->"Doppio",
    "java.version" -> "1.6",
    "java.vendor.url" -> "https ->//github.com/int3/doppio",
    "java.class.version" -> "50.0",
    "java.security.debug" -> "access,failure",
    "java.security.auth.debug" -> "access,failure",
    "java.specification.version" -> "1.6",
    "jdk.map.althashing.threshold" -> "-1",
    "line.separator" ->"\n",
    "file.separator" ->"/",
    "path.separator" ->":",
    "user.dir" -> ".",
    "user.home" ->".",
    "user.name" ->"DoppioUser",
    "os.name" ->"doppio",
    "os.arch" -> "js",
    "os.version" -> "0"*/

  )

  def getObject(x: virt.Val, i: Long): virt.Val = {
    x match{
      case o: virt.Obj =>
        (for {
          m <- o.members
          k <- m.keys.find(_.hashCode == i)
        } yield m(k)).head

      case r: virt.Arr =>
        r.backing(i.toInt)
    }
  }
  def putObject(x: virt.Val, i: Long, b: virt.Val) = {
    x match{
      case o: virt.Obj =>
        for {
          m <- o.members
          k <- m.keys.find(_.hashCode == i)
        } yield m(k) = b
      case r: virt.Arr =>
        r.backing(i.toInt) = b
    }
  }

  val trapped: Natives.NativeMap = {
    Seq(
      "java"/(
        "io"/(
          "FileInputStream"/(
            "initIDs()V" - noOp
            ),
          "FileOutputStream"/(
            "initIDs()V" - noOp
            ),
          "FileDescriptor"/(
            "initIDs()V" - noOp,
            "set(I)J" - {vt => (x: Int) => x.toLong}
            )
          ),
        "lang"/(
          "reflect"/(
            "Array"/(
              "newArray(Ljava/lang/Class;I)Ljava/lang/Object;" - {
                vt =>(x: virt.Cls, n: Int) => new Array[Obj](n)
              }
              )
            ),
          "Class"/(
            "registerNatives()V" - noOp,
            "getName0()L//String;" - {vt => (s: virt.Type) =>
              import vt.vm
              s.tpe.unparse.replace("/", ".")
            },
            "forName0(L//String;)L//Class;" - {vt => (s: Obj) =>
              import vt._
              Type.Cls(virt.Val.unvirtString(s)).obj
            },
            "forName0(L//String;ZL//ClassLoader;)L//Class;" - {vt => (s: virt.Obj, w: Any, y: Any) =>
              import vt._
              Type.Cls(virt.Val.unvirtString(s)).obj
            },
            "getPrimitiveClass(L//String;)L//Class;" - {vt => (s: Obj) =>
              import vt._
              Type.Cls(Type.primitiveMap(virt.Val.unvirtString(s))).obj
            },
            "getClassLoader0()L//ClassLoader;" - value1(null),
            "getDeclaringClass()L//Class;" - value(null),
            "getComponentType()Ljava/lang/Class;" - { vt => (x: virt.Type) =>
              import vt._
              x.tpe match{
                case imm.Type.Arr(inner) => inner.obj
                case _ => null
              }
            },
            "getInterfaces()[Ljava/lang/Class;" - {
              vt => (cls: virt.Cls) => cls.getInterfaces().toArray
            },
            "getDeclaredConstructors0(Z)[Ljava/lang/reflect/Constructor;" - {
              vt => (cls: virt.Cls, b: Int) => cls.getDeclaredConstructors().toArray
            },
            "getDeclaredFields0(Z)[L//reflect/Field;" - { vt => (cls: virt.Type, b: Int) =>
              cls.getDeclaredFields()
            },
            "getDeclaredMethods0(Z)[L//reflect/Method;" - { vt => (cls: virt.Type, b: Int) =>
              cls.getDeclaredMethods()
            },
            "getEnclosingMethod0()[L//Object;" - value(null),
            "getModifiers()I" - { vt => (x: virt.Cls) => import vt.vm._; Type.Cls(x.name).clsData.access_flags },
            "getSuperclass()L//Class;" - { vt => (x: virt.Cls) =>
              import vt.vm
              import vm._
              Type.Cls(x.name).clsData
                .superType
                .map(_.obj)
                .getOrElse(null)
            },
            "getRawAnnotations()[B" - value1(virt.Arr(Type.Arr(Type.Prim("B")), 0)),
            "isPrimitive()Z" - value1(false),
            "isInterface()Z" - { vt => (x: virt.Cls) => import vt.vm._; (Type.Cls(x.name.replace(".", "/")).clsData.access_flags & Access.Interface) != 0},
            "isAssignableFrom(L//Class;)Z" - { vt => (x: virt.Type, y: virt.Type) =>
              true
            },
            "isArray()Z" - ( vt => (_: virt.Type).tpe.isInstanceOf[Type.Arr]),
            "desiredAssertionStatus0(L//Class;)Z" - value1(0)
          ),
          "ClassLoader"/(
            "getSystemClassLoader()L//ClassLoader;" - value(null),
            "getCaller(I)L//Class;" - { vt => (x: Int) =>
              vt.threadStack(x).runningClass.obj
            },
            "getResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;" - { vt => (cl: virt.Obj, s: virt.Obj) =>
              import vt.vm
              val str = virt.Val.unvirtString(s)

              fileLoader(str) match{
                case None => null
                case Some(bytes) =>
                  virt.Obj("java.io.ByteArrayInputStream",
                    "buf" -> bytes,
                    "pos" -> 0,
                    "mark" -> 0,
                    "count" -> bytes.length
                  )
              }
            },
            "getSystemResourceAsStream(Ljava/lang/String;)Ljava/io/InputStream;" - { vt => (s: virt.Obj) =>

              import vt.vm
              val str = virt.Val.unvirtString(s)

              fileLoader(str) match{
                case None => null
                case Some(bytes) =>
                  virt.Obj("java.io.ByteArrayInputStream",
                    "buf" -> bytes,
                    "pos" -> 0,
                    "mark" -> 0,
                    "count" -> bytes.length
                  )
              }


            }

          ),
          "Double"/(
            "doubleToRawLongBits(D)J"-{ vt => java.lang.Double.doubleToRawLongBits(_: Double)},
            "longBitsToDouble(J)D"-{ vt => java.lang.Double.longBitsToDouble(_: Long)}
          ),
          "Float"/(
            "intBitsToFloat(I)F"-{ vt => java.lang.Float.intBitsToFloat(_: Int)},
            "floatToRawIntBits(F)I"-{ vt => java.lang.Float.floatToIntBits(_: Float)}
          ),
          "Object"/(
            "clone()L//Object;" -  {vt => (_: Any ) match{
              case (x: Obj) => x
              case (a: Array[_]) => a.clone
            }},
            "registerNatives()V" - noOp,
            "getClass()L//Class;" - { vt => (_: Any) match{
              case (x: Obj) => x.cls.obj
              case a: Array[_] => import vt.vm; Type.Arr.read(a.getClass.getName).obj
            }},
            "hashCode()I" - { vt => (_: Obj).hashCode()},
            "notify()V" - noOp1,
            "notifyAll()V" - noOp1
            ),
          "Runtime"/(
            "freeMemory()J" - value(1000000000L),
            "availableProcessors()I" - value(1)
            ),
          "String"/(
            "intern()L//String;" - (vt => (x: virt.Obj) => vt.vm.InternedStrings(x))
            ),
          "System"/(
            "arraycopy(L//Object;IL//Object;II)V" - { vt => (src: virt.Arr, srcPos: Int, dest: virt.Arr, destPos: Int, length: Int) =>

              System.arraycopy(src.backing, srcPos, dest.backing, destPos, length)
            },
            "currentTimeMillis()J" - value(System.currentTimeMillis()),
            "getProperty(L//String;)L//String;" - {
              vt => (s: Obj) =>
                import vt.vm
                properties.get(virt.Val.unvirtString(s))
                          .map(x => x: virt.Val)
                          .getOrElse(virt.Null)

            },
            "getProperty(L//String;L//String;)L//String;" - {
              vt => (s: Obj, dflt: Obj) =>
                import vt.vm
                properties.get(virt.Val.unvirtString(s))
                          .map(x => x: virt.Val)
                          .getOrElse(dflt)
            },
            "nanoTime()J" - value(System.nanoTime()),
            "initProperties(Ljava/util/Properties;)Ljava/util/Properties;" - value1(null),
            "identityHashCode(L//Object;)I"-( vt => (x: Obj) => System.identityHashCode(x)),
            "registerNatives()V" - noOp,
            "setIn0(Ljava/io/InputStream;)V" - noOp1,
            "setOut0(Ljava/io/PrintStream;)V" - noOp1,
            "setErr0(Ljava/io/PrintStream;)V" - noOp1
            ),
          "Thread"/(
            "currentThread()L//Thread;" - {vt => vt.obj},
            "isAlive()Z" - value(false),
            "setPriority0(I)V" - noOp2,
            "start0()V" - noOp
            ),
          "Throwable"/(
            "fillInStackTrace(I)L//Throwable;" - { vt => (throwable: Obj, dummy: Int) =>
              import vt.vm;
              import vm._
              throwable.members(0)("stackTrace") =
                vt.getStackTrace.map { f =>
                  virt.Obj("java/lang/StackTraceElement",
                    "declaringClass" -> f.getClassName,
                    "methodName" -> f.getMethodName,
                    "fileName" -> f.getFileName,
                    "lineNumber" -> f.getLineNumber
                  )
                }.toArray
              throwable
            }
          ),
          "ref"/(
            "Reference$ReferenceHandler"/(
              "isAlive()Z" - value(false)
              )
            )
          ),
        "security"/(
          "AccessController"/(
            "doPrivileged(L//PrivilegedAction;)L/lang/Object;" - {
              vt => (pa: Obj) =>
                vt.prepInvoke(pa.cls.clsData.tpe, "run", pa.cls.clsData.methods.find(_.name == "run").get.desc, Seq(pa))
            },
            "getStackAccessControlContext()L//AccessControlContext;" - { vt => virt.Obj("java/security/AccessControlContext")(vt.vm)},
            "getInheritedAccessControlContext()L//AccessControlContext;" - { vt => virt.Obj("java/security/AccessControlContext")(vt.vm)}
            )
          )
        ),
      "scala"/(
        "Predef$"/(
          "println(Ljava/lang/String;)V" - {
            vt => (x: virt.Obj, y: virt.Obj) => vt.vm.log("VIRTUAL " + virt.Val.unvirtString(y))
          },
          "println(Ljava/lang/Object;)V" - {
            vt => (x: virt.Obj, y: virt.Obj) => vt.vm.log("VIRTUAL " + virt.Val.unvirtString(y))
          }

        )
      ),
      "sun"/(
        "misc"/(
          "Hashing"/(
            "randomHashSeed(Ljava/lang/Object;)I" - value1(1)
          ),
          "Unsafe"/(
            "arrayBaseOffset(Ljava/lang/Class;)I" - value1(0),
            "arrayIndexScale(Ljava/lang/Class;)I" - value1(1),
            "addressSize()I" - ((x: Any) => 4),
            "compareAndSwapInt(Ljava/lang/Object;JII)Z" - { vt => (unsafe: Any, a: Obj, i: Long, b: Int, c: Int) =>
              if (getObject(a, i) == b) {
                putObject(a, i, b)
                true
              }else{
                false
              }
              true
            },
            "putOrderedObject(Ljava/lang/Object;JLjava/lang/Object;)V" - {
              vt => (unsafe: Obj, a: Any, i: Long, b: Any) => putObject(a, i, b)
            },
            "getObject(Ljava/lang/Object;J)Ljava/lang/Object;" - {
              vt => (unsafe: Obj, a: Any, i: Long) => getObject(a, i)
            },
            "getObjectVolatile(Ljava/lang/Object;J)Ljava/lang/Object;" - {
              vt => (unsafe: Obj, a: Any, i: Long) => getObject(a, i)
            },
            "getUnsafe()Lsun/misc/Unsafe;" - {vt =>
              import vt._
              virt.Obj("sun/misc/Unsafe")
            },
            "compareAndSwapObject(Ljava/lang/Object;JLjava/lang/Object;Ljava/lang/Object;)Z" - {
              vt => (unsafe: Obj, a: Any, i: Long, b: Any, c: Any) =>
                if (getObject(a, i) == b) {
                  putObject(a, i, b)
                  true
                }else{
                  false
                }
            },
            "objectFieldOffset(Ljava/lang/reflect/Field;)J" - { vt => (unsafe: Any, x: Obj) =>
              x(Type.Cls("java/lang/reflect/Field"), "slot").asInstanceOf[Int].toLong
            }
          ),
          "VM"/(
            "initialize()V" - noOp,
            "isBooted()Z" - value(true),
            "getSavedProperty(Ljava/lang/String;)Ljava/lang/String;" - {
              vt => (s: Obj) =>
                properties.get(virt.Val.unvirtString(s))
                          .map(x => x: virt.Val)
                          .getOrElse(virt.Null)
            }
          )

        ),

        "reflect"/(
          "NativeConstructorAccessorImpl"/(
            "newInstance0(Ljava/lang/reflect/Constructor;[Ljava/lang/Object;)Ljava/lang/Object;" - {
              vt => (constr: Obj, args: Array[Obj]) =>
                import vt.vm; import vm._
                val cls: sm.Cls = imm.Type.Cls(constr.members(0)("clazz").asInstanceOf[virt.Cls].name)
                val newObj = new Obj(cls)
                vt.invoke(cls.clsData.tpe, "<init>", Type.Desc.read("()V"), Seq(newObj))
                newObj
            }
            ),
          "Reflection"/(
            "getCallerClass(I)Ljava/lang/Class;" - { vt => (n: Int) =>
              import vt.vm; import vm._
              Type.Cls(vt.getStackTrace.drop(n).head.getClassName).obj
            },
            "getClassAccessFlags(Ljava/lang/Class;)I" - { vt => (x: virt.Cls) =>
              import vt.vm._;
              Type.Cls(x.name).clsData.access_flags
            },
            "registerMethodsToFilter(Ljava/lang/Class;[Ljava/lang/String;)V" - noOp2
          )
        )
      )
    ).toRoute().toMap
  }





}

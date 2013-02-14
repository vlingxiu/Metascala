package svm.model.opcodes

import OpCodeGen
import OpCodeGen._
import OpCodeGen.ReturnStuff
import OpCodeGen.JsrBranch
import OpCodeGen.BinaryBranch
import OpCodeGen.PushFromArray
import OpCodeGen.StoreArray
import OpCodeGen.PushValOpCode
import OpCodeGen.UnaryBranch
import OpCodeGen.PureStackOpCode
import OpCodeGen.StoreLocal
import OpCodeGen.PushOpCode
import OpCodeGen.PushLocalIndexed
import OpCodeGen.RetBranch
import OpCodeGen.PushConstOpCode
import svm.model.immutable.ConstantInfo.FieldRef
import svm.model.immutable.ConstantInfo


object OpCodes {

  import OpCodeGen._
  implicit def intToByte(n: I) = n.toByte

  type I = Int
  type L = Long
  type F = Float
  type D = Double
  val Nop = OpCode(0, "nop"){ (_, _) => ()}
  val AConstNull = PushOpCode(1, "aconst_null", null)
  val IConstNull = PushOpCode(2, "iconst_m1", -1)

  val IConst0 = PushOpCode(3, "iconst_0", 0)
  val IConst1 = PushOpCode(4, "iconst_1", 1)
  val IConst2 = PushOpCode(5, "iconst_2", 2)
  val IConst3 = PushOpCode(6, "iconst_3", 3)
  val IConst4 = PushOpCode(7, "iconst_4", 4)
  val IConst5 =  PushOpCode(8, "iconst_5", 5)

  val LConst0 = PushOpCode(9, "lconst_0", 0L)
  val LConst1 = PushOpCode(10, "lconst_1", 1L)

  val FConst0 = PushOpCode(11, "fconst_0", 0f)
  val FConst1 = PushOpCode(12, "fconst_1", 1f)
  val FConst2 = PushOpCode(13, "fconst_2", 2f)

  val DConst0 = PushOpCode(14, "dconst_0", 0d)
  val DConst1 = PushOpCode(15, "dconst_1", 1d)

  val BiPush = PushValOpCode(16, "bipush", b => b(1))
  val SiPush = PushValOpCode(17,"sipush", b => b(1) << 8 + b(2))

  val Ldc = PushConstOpCode(18, "ldc", b => b(1))
  val LdcW = PushConstOpCode(19, "ldc_w", b => b(1) << 8 + b(2))
  val Ldc2W = PushConstOpCode(20, "ldc2_w", b => b(1) << 8 + b(2))

  val ILoad = PushLocalIndexed(21, "iLoad", -1)
  val LLoad = PushLocalIndexed(22, "lLoad", -1)
  val FLoad = PushLocalIndexed(23, "fLoad", -1)
  val DLoad = PushLocalIndexed(24, "dLoad", -1)
  val ALoad = PushLocalIndexed(25, "aLoad", -1)

  val ILoad0 = PushLocalIndexed(26, "iLoad_0", 0)
  val ILoad1 = PushLocalIndexed(27, "iLoad_1", 1)
  val ILoad2 = PushLocalIndexed(28, "iLoad_2", 2)
  val ILoad3 = PushLocalIndexed(29, "iLoad_3", 3)

  val LLoad0 = PushLocalIndexed(30, "lLoad_0", 0)
  val LLoad1 = PushLocalIndexed(31, "lLoad_1", 1)
  val LLoad2 = PushLocalIndexed(32, "lLoad_2", 2)
  val LLoad3 = PushLocalIndexed(33, "lLoad_3", 3)

  val FLoad0 = PushLocalIndexed(34, "fLoad_0", 0)
  val FLoad1 = PushLocalIndexed(35, "fLoad_1", 1)
  val FLoad2 = PushLocalIndexed(36, "fLoad_2", 2)
  val FLoad3 = PushLocalIndexed(37, "fLoad_3", 3)

  val DLoad0 = PushLocalIndexed(38, "dLoad_0", 0)
  val DLoad1 = PushLocalIndexed(39, "dLoad_1", 1)
  val DLoad2 = PushLocalIndexed(40, "dLoad_2", 2)
  val DLoad3 = PushLocalIndexed(41, "dLoad_3", 3)

  val ALoad0 = PushLocalIndexed(42, "aLoad_0", 0)
  val ALoad1 = PushLocalIndexed(43, "aLoad_1", 1)
  val ALoad2 = PushLocalIndexed(44, "aLoad_2", 2)
  val ALoad3 = PushLocalIndexed(45, "aLoad_3", 3)

  val IALoad = PushFromArray(46, "iaLoad")
  val LALoad = PushFromArray(47, "laLoad")
  val FALoad = PushFromArray(48, "faLoad")
  val DALoad = PushFromArray(49, "daLoad")
  val AALoad = PushFromArray(50, "aaLoad")
  val BALoad = PushFromArray(51, "baLoad")
  val CALoad = PushFromArray(52, "caLoad")
  val SALoad = PushFromArray(53, "saLoad")

  val IStore = StoreLocal(54, "istore", -1)
  val LStore = StoreLocal(55, "lstore", -1)
  val FStore = StoreLocal(56, "fstore", -1)
  val DStore = StoreLocal(57, "dstore", -1)
  val AStore = StoreLocal(58, "astore", -1)

  val IStore0 = StoreLocal(59, "istore_0", 0)
  val IStore1 = StoreLocal(60, "istore_1", 1)
  val IStore2 = StoreLocal(61, "istore_2", 2)
  val IStore3 = StoreLocal(62, "istore_3", 3)

  val LStore0 = StoreLocal(63, "lstore_0", 0)
  val LStore1 = StoreLocal(64, "lstore_1", 1)
  val LStore2 = StoreLocal(65, "lstore_2", 2)
  val LStore3 = StoreLocal(66, "lstore_3", 3)

  val FStore0 = StoreLocal(67, "fstore_0", 0)
  val FStore1 = StoreLocal(68, "fstore_1", 1)
  val FStore2 = StoreLocal(69, "fstore_2", 2)
  val FStore3 = StoreLocal(70, "fstore_3", 3)

  val DStore0 = StoreLocal(71, "dstore_0", 0)
  val DStore1 = StoreLocal(72, "dstore_1", 1)
  val DStore2 = StoreLocal(73, "dstore_2", 2)
  val DStore3 = StoreLocal(74, "dstore_3", 3)

  val AStore0 = StoreLocal(75, "astore_0", 0)
  val AStore1 = StoreLocal(76, "astore_1", 1)
  val AStore2 = StoreLocal(77, "astore_2", 2)
  val AStore3 = StoreLocal(78, "astore_3", 3)

  val IAStore = StoreArray(79, "iastore")
  val LAStore = StoreArray(80, "lastore")
  val FAStore = StoreArray(81, "fastore")
  val DAStore = StoreArray(82, "dastore")
  val AAStore = StoreArray(83, "aastore")
  val BAStore = StoreArray(84, "bastore")
  val CAStore = StoreArray(85, "castore")
  val SAStore = StoreArray(86, "sastore")

  val Pop = PureStackOpCode(87, "pop"){ case _ :: s => s }
  val Pop2 = PureStackOpCode(88, "pop2"){ case _ :: _ :: s => s }
  val Dup = PureStackOpCode(89, "dup"){ case top :: s => top :: top :: s }
  val DupX1 = PureStackOpCode(90, "dup_x1"){ case top :: x :: s => top :: x :: top :: s }
  val DupX2 = PureStackOpCode(91, "dup_x2"){ case top :: y :: x :: s => top :: y :: x :: top :: s }
  val Dup2 = PureStackOpCode(92, "dup2"){ case y :: x :: s => y :: x :: y :: x :: s }
  val Dup2X1 = PureStackOpCode(93, "dup2_x1"){ case a :: b :: x :: s => a :: b :: x :: a :: b :: s }
  val Dup2X2 = PureStackOpCode(94, "dup2_x2"){ case a :: b :: x :: y :: s => a :: b :: x :: y :: a :: b :: s }
  val Swap = PureStackOpCode(95, "swap"){ case x :: y :: s=> y :: x :: s }

  val IAdd = PureStackOpCode(96, "iadd"){ case (x: I) :: (y: I) :: s => (x + y) :: s}
  val LAdd = PureStackOpCode(97, "ladd"){ case (x: L) :: (y: L) :: s => (x + y) :: s}
  val FAdd = PureStackOpCode(98, "fadd"){ case (x: F) :: (y: F) :: s => (x + y) :: s}
  val DAdd = PureStackOpCode(99, "dadd"){ case (x: D) :: (y: D) :: s => (x + y) :: s}

  val ISub = PureStackOpCode(100, "isub"){ case (x: I) :: (y: I) :: s => (x - y) :: s}
  val LSub = PureStackOpCode(101, "lsub"){ case (x: L) :: (y: L) :: s => (x - y) :: s}
  val FSub = PureStackOpCode(102, "fsub"){ case (x: F) :: (y: F) :: s => (x - y) :: s}
  val DSub = PureStackOpCode(103, "dsub"){ case (x: D) :: (y: D) :: s => (x - y) :: s}

  val IMul = PureStackOpCode(104, "imul"){ case (x: I) :: (y: I) :: s => (x * y) :: s}
  val LMul = PureStackOpCode(105, "lmul"){ case (x: L) :: (y: L) :: s => (x * y) :: s}
  val FMul = PureStackOpCode(106, "fmul"){ case (x: F) :: (y: F) :: s => (x * y) :: s}
  val DMul = PureStackOpCode(107, "dmul"){ case (x: D) :: (y: D) :: s => (x * y) :: s}

  val IDiv = PureStackOpCode(108, "idiv"){ case (x: I) :: (y: I) :: s => (x / y) :: s}
  val LDiv = PureStackOpCode(109, "ldiv"){ case (x: L) :: (y: L) :: s => (x / y) :: s}
  val FDiv = PureStackOpCode(110, "fdiv"){ case (x: F) :: (y: F) :: s => (x / y) :: s}
  val DDiv = PureStackOpCode(111, "ddiv"){ case (x: D) :: (y: D) :: s => (x / y) :: s}

  val IRem = PureStackOpCode(112, "irem"){ case (x: I) :: (y: I) :: s => (x % y) :: s}
  val LRem = PureStackOpCode(113, "lrem"){ case (x: L) :: (y: L) :: s => (x % y) :: s}
  val FRem = PureStackOpCode(114, "frem"){ case (x: F) :: (y: F) :: s => (x % y) :: s}
  val DRem = PureStackOpCode(115, "drem"){ case (x: D) :: (y: D) :: s => (x % y) :: s}

  val INeg = PureStackOpCode(116, "ineg"){ case (x: I) :: s => -x :: s }
  val LNeg = PureStackOpCode(117, "lneg"){ case (x: L) :: s => -x :: s }
  val FNeg = PureStackOpCode(118, "fneg"){ case (x: F) :: s => -x :: s }
  val DNeg = PureStackOpCode(119, "dneg"){ case (x: D) :: s => -x :: s }

  val IShl = PureStackOpCode(120, "ishl"){ case (x: I) :: (y: I) :: s => (x << y) :: s }
  val LShl = PureStackOpCode(121, "lshl"){ case (x: L) :: (y: L) :: s => (x << y) :: s }
  val IShr = PureStackOpCode(122, "ishr"){ case (x: I) :: (y: I) :: s => (x >> y) :: s }
  val LShr = PureStackOpCode(123, "lshr"){ case (x: L) :: (y: L) :: s => (x >> y) :: s }

  val IUShr = PureStackOpCode(124, "iushr"){ case (x: I) :: (y: I) :: s => (x >>> y) :: s }
  val LUShr = PureStackOpCode(125, "lushr"){ case (x: L) :: (y: L) :: s => (x >>> y) :: s }

  val IAnd = PureStackOpCode(126, "iand"){ case s :+ (x: I) :+ (y: I) => s :+ (x & y) }
  val LAnd = PureStackOpCode(127, "land"){ case s :+ (x: L) :+ (y: I) => s :+ (x & y) }

  val IOr = PureStackOpCode(128, "ior"){ case s :+ (x: I) :+ (y: I) => s :+ (x | y) }
  val LOr = PureStackOpCode(129, "lor"){ case s :+ (x: L) :+ (y: I) => s :+ (x | y) }

  val IXOr = PureStackOpCode(130, "ixor"){ case s :+ (x: I) :+ (y: I) => s :+ (x ^ y) }
  val LXOr = PureStackOpCode(131, "lxor"){ case s :+ (x: L) :+ (y: I) => s :+ (x ^ y) }

  val IInc = OpCode(132, "iinc"){ case (Seq(index, const), ctx) => ctx.frame.locals = ctx.frame.locals.updated(index, ctx.frame.locals(index).asInstanceOf[Int] + const)}

  val I2L = PureStackOpCode(133, "i2l"){ case (x: I) :: s => x.toLong :: s}
  val I2F = PureStackOpCode(134, "i2f"){ case (x: I) :: s => x.toFloat :: s }
  val I2D = PureStackOpCode(135, "i2d"){ case (x: I) :: s => x.toDouble :: s }

  val L2I = PureStackOpCode(136, "l2i"){ case (x: L) :: s => x.toInt :: s }
  val L2F = PureStackOpCode(137, "l2f"){ case (x: L) :: s => x.toFloat :: s }
  val L2D = PureStackOpCode(138, "l2d"){ case (x: L) :: s => x.toDouble :: s }

  val F2I = PureStackOpCode(139, "f2i"){ case (x: F) :: s => x.toInt :: s }
  val F2L = PureStackOpCode(140, "f2l"){ case (x: F) :: s => x.toLong :: s }
  val F2D = PureStackOpCode(141, "f2d"){ case (x: F) :: s => x.toDouble :: s }

  val D2I = PureStackOpCode(142, "d2i"){ case (x: D) :: s => x.toInt :: s }
  val D2L = PureStackOpCode(143, "d2l"){ case (x: D) :: s => x.toLong :: s }
  val D2F = PureStackOpCode(144, "d2f"){ case (x: D) :: s  => x.toFloat :: s }

  val I2B = PureStackOpCode(145, "i2b"){ case (x: I) :: s => x.toByte :: s }
  val I2C = PureStackOpCode(146, "i2c"){ case (x: I) :: s => x.toChar :: s }
  val I2S = PureStackOpCode(147, "i2s"){ case (x: I) :: s => x.toShort :: s }

  val LCmp = PureStackOpCode(148, "lcmp"){ case (x: L) :: (y: L) :: s => x.compare(y) :: s }
  val FCmpl = PureStackOpCode(149, "fcmpl"){ case (x: F) :: (y: F) :: s => x.compare(y) :: s }
  val FCmpg = PureStackOpCode(150, "fcmpg"){ case (x: F) :: (y: F) :: s => x.compare(y) :: s }
  val DCmpl = PureStackOpCode(151, "dcmpl"){ case (x: D) :: (y: D) :: s => x.compare(y) :: s }
  val DCmpg = PureStackOpCode(152, "dcmpg"){ case (x: D) :: (y: D) :: s => x.compare(y) :: s }

  val IfEq = UnaryBranch(153, "ifeq")(_ == 0)
  val IfNe = UnaryBranch(154, "ifne")(_ != 0)
  val IfLt = UnaryBranch(155, "iflt")(_ < 0)
  val IfGe = UnaryBranch(156, "ifge")(_ >= 0)
  val IfGt = UnaryBranch(157, "ifgt")(_ > 0)
  val IfLe = UnaryBranch(158, "ifle")(_ <= 0)

  val IfICmpEq = BinaryBranch(159, "if_icmpeq")(_ == _)
  val IfICmpNe = BinaryBranch(160, "if_icmpne")(_ != _)
  val IfICmpLt = BinaryBranch(161, "if_icmplt")(_ < _)
  val IfICmpGe = BinaryBranch(162, "if_icmpge")(_ >= _)
  val IfICmpGt = BinaryBranch(163, "if_icmpgt")(_ > _)
  val IfICmpLe = BinaryBranch(164, "if_icmple")(_ <= _)
  val IfACmpEq = BinaryBranch(165, "if_acmpeq")(_ == _)
  val IfACmpNe = BinaryBranch(166, "if_acmpne")(_ != _)

  val Goto = UnaryBranch(167, "goto"){x => true}
  val Jsr = JsrBranch(168, "jsr")
  val Ret = RetBranch(169, "ret")
  val TableSwitch = OpCode(170, "tableswitch")(???)
  val LookupSwitch = OpCode(171, "lookupswitch")(???)

  val IReturn = ReturnStuff(172, "ireturn")
  val LReturn = ReturnStuff(173, "lreturn")
  val FReturn = ReturnStuff(174, "freturn")
  val DReturn = ReturnStuff(175, "dreturn")
  val AReturn = ReturnStuff(176, "areturn")
  val Return = ReturnStuff(177, "return")

  val GetStatic = StackOpCode(178, "getstatic"){case (Seq(b1, b2), ctx, stack) =>
    val index = (b1 << 8) | b2
    val FieldRef(classIndex, nameAndTypeIndex) = ctx.frame.constantPool(index)
    val ConstantInfo.Class(nameIndex) = ctx.frame.constantPool(classIndex)
    ???
  }
  val PutStatic = OpCode(179, "putstatic")(???)

  val GetField = OpCode(180, "getfield")(???)
  val PutField = OpCode(181, "putfield")(???)

  val InvokeVirtual = OpCode(182, "invokevirtual")(???)
  val InvokeSpecial = OpCode(183, "invokespecial")(???)
  val InvokeStatic = OpCode(184, "invokestatic")(???)
  val InvokeInterface = OpCode(185, "invokeinterface")(???)
  val InvokeDynamic = OpCode(186, "invokedynamic")(???)

  val New = OpCode(187, "new")(???)
  val NewArray = OpCode(188, "newarray")(???)
  val ANewArray = OpCode(189, "anewarray")(???)

  val ArrayLength = OpCode(190, "arraylength")(???)
  val AThrow = OpCode(191, "athrow")(???)
  val CheckCast = OpCode(192, "checkcast")(???)
  val InstanceOf = OpCode(193, "instanceof")(???)
  val MonitorEnter = OpCode(194, "monitorenter")(???)
  val MonitorExit = OpCode(195, "monitorexit")(???)
  val Wide = OpCode(196, "wide")(???)
  val MultiANewArray = OpCode(197, "multianewarray")(???)

  val IfNull = OpCode(198, "ifnull")(???)
  val IfNonNull = OpCode(199, "ifnonnull")(???)

  val GotoW = OpCode(200, "goto_w")(???)
  val JsrW = OpCode(201, "jsr_w")(???)

  val all = Seq(
    Nop,
    AConstNull,
    IConstNull,
    IConst0,
    IConst1,
    IConst2,
    IConst3,
    IConst4,
    IConst5,
    LConst0,
    LConst1,
    FConst0,
    FConst1,
    FConst2,
    DConst0,
    DConst1,
    BiPush,
    SiPush,
    Ldc,
    LdcW,
    Ldc2W,

    IStore,
    LStore,
    FStore,
    DStore,
    AStore,

    IStore0,
    IStore1,
    IStore2,
    IStore3,

    LStore0,
    LStore1,
    LStore2,
    LStore3,

    FStore0,
    FStore1,
    FStore2,
    FStore3,

    DStore0,
    DStore1,
    DStore2,
    DStore3,

    AStore0,
    AStore1,
    AStore2,
    AStore3,

    IAStore,
    LAStore,
    FAStore,
    DAStore,
    AAStore,
    BAStore,
    CAStore,
    SAStore,

    IStore,
    LStore,
    FStore,
    DStore,
    AStore,

    IStore0,
    IStore1,
    IStore2,
    IStore3,

    FStore0,
    FStore1,
    FStore2,
    FStore3,

    DStore0,
    DStore1,
    DStore2,
    DStore3,

    AStore0,
    AStore1,
    AStore2,
    AStore3,


    IAStore,
    LAStore,
    FAStore,
    DAStore,
    AAStore,
    BAStore,
    CAStore,
    SAStore,

    Pop,
    Pop2,
    Dup,
    DupX1,
    DupX2,
    Dup2,
    Dup2X1,
    Dup2X2,
    Swap,

    IAdd,
    LAdd,
    FAdd,
    DAdd,

    ISub,
    LSub,
    FSub,
    DSub,

    IMul,
    LMul,
    FMul,
    DMul,

    IDiv,
    LDiv,
    FDiv,
    DDiv,

    IRem,
    LRem,
    FRem,
    DRem,

    INeg,
    LNeg,
    FNeg,
    DNeg,

    IShl,
    LShl,
    IShr,
    LShr,

    IUShr,
    LUShr,

    IAnd,
    LAnd,

    IOr,
    LOr,

    IXOr,
    LXOr,

    IInc,

    I2L,
    I2F,
    I2D,

    L2I,
    L2F,
    L2D,
    F2I,
    F2L,
    F2D,

    D2I,
    D2L,
    D2F,

    I2B,
    I2C,
    I2S,

    LCmp,
    FCmpl,
    FCmpg,
    DCmpl,
    DCmpg,

    IfEq,
    IfNe,
    IfLt,
    IfGe,
    IfGt,
    IfLe,

    IfICmpEq,
    IfICmpNe,
    IfICmpLt,
    IfICmpGe,
    IfICmpGt,
    IfICmpLe,
    IfACmpEq,
    IfACmpNe,

    Goto,
    Jsr,
    Ret,
    TableSwitch,
    LookupSwitch,
    IReturn,
    LReturn,
    FReturn,
    DReturn,
    AReturn,
    Return,
    GetStatic,
    PutStatic,
    GetField,
    PutField,
    InvokeVirtual,
    InvokeSpecial,
    InvokeStatic,
    InvokeInterface,
    InvokeDynamic,

    New,
    NewArray,
    ANewArray,

    ArrayLength,
    AThrow,
    CheckCast,
    InstanceOf,
    MonitorEnter,
    MonitorExit,
    Wide,
    MultiANewArray,
    IfNull,
    IfNonNull,
    GotoW,
    JsrW
  )




}




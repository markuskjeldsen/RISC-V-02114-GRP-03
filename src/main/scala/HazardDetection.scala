import chisel3._
import chisel3.util._

class HazardDetection() extends Module{
  val io = IO(new Bundle {
    val in = Input(new Bundle {
      val IFIDinstruction = Input(UInt(32.W))
      val IDEXinstruction = Input(UInt(32.W))
      val EXMEMinstruction = Input(UInt(32.W))
      val MEMWBinstruction = Input(UInt(32.W))

    })

    val out = Output(new Bundle {
      val IFIDen = Output(Bool())
      val IFIDclear = Output(Bool())

      val IDEXen = Output(Bool())
      val IDEXclear = Output(Bool())

      val PCen = Output(Bool())
    })
  })

  // Corrected bit extraction: (High, Low)
  val IFIDrs1    = io.in.IFIDinstruction(19, 15)
  val IFIDrs2    = io.in.IFIDinstruction(24, 20)
  val IFIDrd     = io.in.IFIDinstruction(11, 7)
  val IFIDopcode = io.in.IFIDinstruction(6, 0) // RISC-V opcodes are 7 bits (6:0)

  val IDEXrs1    = io.in.IDEXinstruction(19, 15)
  val IDEXrs2    = io.in.IDEXinstruction(24, 20)
  val IDEXrd     = io.in.IDEXinstruction(11, 7)
  val IDEXopcode = io.in.IDEXinstruction(6, 0) // Fixed reference to IDEXinstruction

  val EXMEMrs1    = io.in.EXMEMinstruction(19, 15)
  val EXMEMrs2    = io.in.EXMEMinstruction(24, 20)
  val EXMEMrd     = io.in.EXMEMinstruction(11, 7)
  val EXMEMopcode = io.in.EXMEMinstruction(6, 0)

  val MEMWBrs1    = io.in.MEMWBinstruction(19, 15)
  val MEMWBrs2    = io.in.MEMWBinstruction(24, 20)
  val MEMWBrd     = io.in.MEMWBinstruction(11, 7)
  val MEMWBopcode = io.in.MEMWBinstruction(6, 0)


  val MemoryStoreInstruction = "b0100011".U
  val MemoryLoadInstruction = "b0000011".U
  val BranchInstruction = "b1100011".U


  // standard output state
  io.out.PCen := 1.U

  io.out.IFIDen := 1.U
  io.out.IFIDclear := 0.U

  io.out.IDEXen := 1.U
  io.out.IDEXclear := 0.U



  // Structual Hazard // A required resource is busy


  // Data hazard // Need to wait for previous instruction to complete its data read/write
  // if next instruction needs the output of the previous

  // if IDEX does not have an output then no need for DataHazard
  when(IDEXopcode =/= MemoryStoreInstruction && IDEXopcode =/= BranchInstruction) {
    // If opcode is NOT a store AND it is NOT a branch...
    // Your hazard/forwarding logic here
  }


    // if IFID has input that is output of IDEX register
    // then stop PC
    // disable IFID
    // clear and disable the IDEX, no new instruction is loaded into IDEX and it clears the output
    // keep this for 2 cycles




  // Control Hazard // Deciding on control action depends on previous instruction



  // Load-Use Data Hazard
  // Can’t always avoid stalls by forwarding
  // If value not computed when needed
  // Can’t forward backward in time!

  // ld x1 0, x2
  // sub x4, x1, x5

  // if no forwarding then 2 cycles stalled.
  // since WB must be done, before anything can be done IFID must be hold and PC must be held as well

  // when load use, hold PC and IFID for 2 cycles
  when(IDEXopcode === MemoryLoadInstruction && IDEXrd =/= 0.U &&
      (IDEXrd === IFIDrs1 || IDEXrd === IFIDrs2)) {
      // stall for 1 cycles
    io.out.PCen := 0.U

    io.out.IFIDen := 0.U
    io.out.IFIDclear := 0.U

    io.out.IDEXen := 1.U
    io.out.IDEXclear := 1.U
  }
  // when load use, hold PC and IFID for 1 cycles
  when(EXMEMopcode === MemoryLoadInstruction && // if memory load
      (EXMEMrd === IFIDrs1 || EXMEMrd === IFIDrs2)) { // and the next instruction will use the output
      // stall for 1 cycle
    io.out.PCen := 0.U

    io.out.IFIDen := 0.U
    io.out.IFIDclear := 0.U

    io.out.IDEXen := 1.U
    io.out.IDEXclear := 1.U
  }



}
import chisel3._
import chisel3.util._

class HazardDetection() extends Module{
  val io = IO(new Bundle {
    // IFID instruction
    // IDEX instruction
    val in = IO(new Bundle {
      val IFIDinstruction = input(UInt(32.W))

      val IDEXinstruction = input(UInt(32.W))

    })

    val out = IO(new Bundle {
      val IFIDen = Output(Bool())
      val IFIDclear = Output(Bool())

      val IDEXen = Output(Bool())
      val IDEXclear = Output(Bool())

      val PCen = Output(Bool())
    })

  })

  val IFIDrs1 = io.in.IFIDinstruction(15,19)
  val IFIDrs2 = io.in.IFIDinstruction(20,24) // only applicable if Branch, Memory Store, Integer register-register instructions
  val IFIDrd = io.in.IFIDinstruction(7,11) // only applicable if not Branch, Memory Store instructions
  val IFIDopcode = io.in.IFIDinstruction(0,7)


  val IDEXrs1 = io.in.IDEXinstruction(15,19)
  val IDEXrs2 = io.in.IDEXinstruction(20,24) // only applicable if Branch, Memory Store, Integer register-register instructions
  val IDEXrd = io.in.IDEXinstruction(7,11) // only applicable if not Branch, Memory Store instructions
  val IDEXopcode = io.in.IFIDinstruction(0,7)

  val MemoryStoreInstruction = "b0100011".U
  val BranchInstruction = "1100011".U



  // Structual Hazard // A required resource is busy


  // Data hazard // Need to wait for previous instruction to complete its data read/write
  // if next instruction needs the output of the previous

  // if IDEX does not have an output then no need for DataHazard
  when( !(IDEXopcode === MemoryStoreInstruction || BranchInstruction) ){
    // if the previous instruction has an output then check if the output address is the same and the input


  }

    // if IFID has input that is output of IDEX register
    // then stop PC
    // disable IFID
    // clear and disable the IDEX, no new instruction is loaded into IDEX and it clears the output
    // keep this for 2 cycles




  // Control Hazard // Deciding on control action depends on previous instruction


}
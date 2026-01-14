package pipelineregisters

import chisel3._

// 1. Define the Bundle provided
class IDEXBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
  val opcode      = UInt(7.W)
  val rs1Data     = UInt(32.W)
  val rs2Data     = UInt(32.W)
  val ALUsrc      = UInt(1.W)
  val imm         = UInt(32.W)
  val ALUctrl     = UInt(4.W)
  val ControlBool = Bool()
  val BranchCtrl = UInt(3.W)
  val ra = UInt(5.W)
  val targetAddress = UInt(5.W)
}

// 2. Define the ID/EX Stage Module
class IDEX extends Module {
  val io = IO(new Bundle {
    val en    = Input(Bool())    // Enable: Update the register (connect to !stall)
    val clear = Input(Bool())    // Clear: Flush the register (set to 0)
    val in    = Input(new IDEXBundle)
    val out   = Output(new IDEXBundle)
  })

  // Initialize the register. On hardware reset, all fields will be 0.
  val reg = RegInit(0.U.asTypeOf(new IDEXBundle))

  // Control Logic
  when(io.clear) {
    // Synchronous Flush: If clear is high, overwrite register with zeros
    reg := 0.U.asTypeOf(new IDEXBundle)
  }.elsewhen(io.en) {
    // Update: If enabled and not cleared, capture the input
    reg := io.in
  }
  // If neither clear nor en is high, the register maintains its previous value

  // Output the current register state
  io.out := reg
}

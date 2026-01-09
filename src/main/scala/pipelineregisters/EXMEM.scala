package pipelineregisters

import chisel3._

// 1. Define the Bundle provided
class EXMEMBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
  val opcode      = UInt(7.W)
  val result      = UInt(32.W)
  val func3       = UInt(3.W)
  val func7       = UInt(7.W)
  val rs2         = UInt(32.W)
}

// 2. Define the EX/MEM Stage Module
class EXMEM extends Module {
  val io = IO(new Bundle {
    val en    = Input(Bool())    // Enable signal (usually high unless stalling)
    val clear = Input(Bool())    // Clear signal (synchronous reset/flush)
    val in    = Input(new EXMEMBundle)
    val out   = Output(new EXMEMBundle)
  })

  // Initialize the pipeline register to zero
  val reg = RegInit(0.U.asTypeOf(new EXMEMBundle))

  // Register Update Logic
  when(io.clear) {
    // Flush the stage: overwrite the register with zeros
    reg := 0.U.asTypeOf(new EXMEMBundle)
  }.elsewhen(io.en) {
    // Capture the ALU result and metadata from the EX stage
    reg := io.in
  }

  // Connect the register to the output ports
  io.out := reg
}

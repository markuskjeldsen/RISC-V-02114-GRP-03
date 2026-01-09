package pipelineregisters

import chisel3._

// 1. Define the Bundle provided
class MEMWBBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
  val opcode      = UInt(7.W)
  val func3       = UInt(3.W)
  val func7       = UInt(7.W)
  val result      = UInt(32.W)
  val memoryVal   = UInt(32.W)
}

// 2. Define the MEM/WB Stage Module
class MEMWB extends Module {
  val io = IO(new Bundle {
    val en    = Input(Bool())    // Enable signal (updates the register)
    val clear = Input(Bool())    // Clear signal (flushes the register)
    val in    = Input(new MEMWBBundle)
    val out   = Output(new MEMWBBundle)
  })

  // Initialize the register to 0.
  // This ensures that on reset, the Writeback stage doesn't perform accidental writes.
  val reg = RegInit(0.U.asTypeOf(new MEMWBBundle))

  // Update Logic
  when(io.clear) {
    // Synchronous flush
    reg := 0.U.asTypeOf(new MEMWBBundle)
  }.elsewhen(io.en) {
    // Capture data from the Memory stage
    reg := io.in
  }

  // Output the values to the Writeback stage
  io.out := reg
}

package pipelineregisters

import chisel3._
import chisel3.util._ // Included for extra utilities if needed

class IFIDBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
}

class IFID extends Module {
  val io = IO(new Bundle {
    val en    = Input(Bool())    // High to update the register (Stall signal inverted)
    val clear = Input(Bool())    // High to flush the register to zero
    val in    = Input(new IFIDBundle)
    val out   = Output(new IFIDBundle)
  })

  // Initialize a register with the Bundle type, starting at 0
  val reg = RegInit(0.U.asTypeOf(new IFIDBundle))

  // Logic:
  // 1. If clear is high, the register resets to 0 (Synchronous flush)
  // 2. Else if enable is high, the register captures the input
  // 3. Otherwise, the register maintains its current value
  when(io.clear) {
    reg := 0.U.asTypeOf(new IFIDBundle)
  }.elsewhen(io.en) {
    reg := io.in
  }

  // Connect the internal register to the output
  io.out := reg
}

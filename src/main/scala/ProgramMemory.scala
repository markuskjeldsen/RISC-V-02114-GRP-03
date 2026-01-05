import chisel3._
import chisel3.util._

class ProgramMemory extends Module {
  val io = IO(new Bundle {
    // Output(UInt(32.W))
    val instruction = Output(UInt(32.W))

    val ProgramCounter = Input(UInt(32.W))
  })

  io.instruction := 0.U
  switch(io.ProgramCounter) {
    is(0.U)  { io.instruction := "0x01e00513".U }
    is(4.U)  { io.instruction := "0x00000013".U }
    is(8.U)  { io.instruction := "0x00000013".U }
    is(12.U) { io.instruction := "0x00000013".U }
    is(16.U) { io.instruction := "0x01e50513".U }
  }
}
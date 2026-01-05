import chisel3._
import chisel3.util._

class ProgramMemory extends Module {
  val io = IO(new Bundle {
    // Output(UInt(32.W))
    val instr = Output(UInt(32.W))

    val ProgramCounter = Input(UInt(32.W))
  })

  io.instr := 0.U
  switch(io.ProgramCounter) {
    is(0.U)  { io.instr := "0x01e00513" }
    is(4.U)  { io.instr := "0x00000013" }
    is(8.U)  { io.instr := "0x00000013" }
    is(12.U) { io.instr := "0x00000013" }
    is(16.U) { io.instr := "0x01e50513" }
  }
}
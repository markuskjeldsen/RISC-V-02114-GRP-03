import chisel3._
import chisel3.util._

class CPU() extends Module {
  val io = IO(new Bundle {
    val PRGCNT = Input(UInt(32.W))
  })

  // Wrap the instantiation in Module()
  val ProgMem = Module(new ProgramMemory())

  ProgMem.io.ProgramCounter := io.PRGCNT
}

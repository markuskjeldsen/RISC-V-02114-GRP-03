import chisel3._
import chisel3.util._



class CPU extends Module {
  val io = IO(new Bundle {
    val PRGCNT = Input(UInt(32.W)) // for now the program counter will be changed externally
  })

  val ProgMem = new ProgramMemory()
  ProgMem.io.ProgramCounter := io.PRGCNT



}
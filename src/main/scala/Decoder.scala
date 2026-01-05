import chisel3._
import chisel3.util._

class decoder() extends Module{
  val io = IO(new Bundle {
    val input = Input(UInt(32.W))
    val outputReg = Output(Uint(5.W))
  })
    val opcode := io.word(6,0)
    val rd := io.word(11,7)
    val f3 := io.word(14,12)
    val rs1 := io.word(19,15)
    val imm := io.word(31,20)
    val io.outputReg := rd


}
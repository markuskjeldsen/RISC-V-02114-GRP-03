import chisel3._
import chisel3.util._

class Decoder() extends Module {
  val io = IO(new Bundle {
    val input  = Input(UInt(32.W))
    val opcode = Output(UInt(7.W))
    val rd     = Output(UInt(5.W))
    val func3  = Output(UInt(3.W))
    val rs1    = Output(UInt(5.W))
    val imm    = Output(UInt(32.W))
  })

  val opcode = io.input(6,0)
  val rd     = io.input(11,7)
  val func3  = io.input(14,12)
  val rs1    = io.input(19,15)

  // currently this is UInt(12.W)
  val rawImm = io.input(31, 20)

  io.opcode := opcode
  io.rd     := rd
  io.func3  := func3
  io.rs1    := rs1

  // 3. Convert to UInt.
  io.imm := rawImm.asSInt.pad(32).asUInt
}

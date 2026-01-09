import chisel3._
import chisel3.util._

class HazardDetection() extends Module{
  val io = IO(new Bundle {
    // IFID instruction
    // IDEX instruction

    val sel = Input(UInt(4.W))
    val a0 = Input(UInt(32.W))
    val a1 = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })

}
import chisel3._
import chisel3.util._

class ALU() extends Module{
  val io = IO(new Bundle {
    val sel = Input(UInt(4.W))
    val a0 = Input(UInt(32.W))
    val a1 = Input(UInt(32.W))
    val out = Output(UInt(32.W))
  })
  io.out := 0.U
  switch(io.sel){
    is(0.U) { io.out := io.a0 + io.a1 } //ADD
    is(1.U) { io.out := io.a0 - io.a1 } //SUB
    is(2.U) { io.out := io.a0 << io.a1(4,0)} //SLL
    is(3.U) { io.out := (io.a0.asSInt < io.a1.asSInt).asUInt} //SLT
    is(4.U) { io.out := (io.a0 < io.a1).asUInt} //SLTU
    is(5.U) { io.out := io.a0 ^ io.a1} //XOR
    is(6.U) { io.out := io.a0 >> io.a1(4,0)} //SRL
    is(7.U) { io.out := (io.a0.asSInt >> io.a1(4,0)).asUInt } // SRA
    is(8.U) { io.out := io.a0 | io.a1 } //OR
    is(9.U) { io.out := io.a0 & io.a1 } //AND
  }
}
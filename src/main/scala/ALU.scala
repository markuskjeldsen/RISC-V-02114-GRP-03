import chisel3._
import chisel3.util._

class ALU() extends Module{
  val io = IO(new Bundle {
    val sel = Input(UInt(4.W))
    val a0 = Input(SInt(32.W))
    val a1 = Input(SInt(32.W))
    val out = Output(SInt(32.W))
  })
  io.out := 0.S
  switch(io.sel){
    is(0.U) { io.out := io.a0 + io.a1 }
    is(0.U) { io.out := io.a0 - io.a1 }
    is(2.U) { io.out := io.a0 << io.a1(4,0)}
    is(3.U) { io.out := (io.a0.asSInt < io.a1.asSInt).asUInt}
    is(4.U) { io.out := (io.a0 < io.a1).asUInt}
    is(5.U) { io.out := io.a0 ^ io.a1}
    is(6.U) { io.out := io.a0 >> io.a1(4,0)}
    is(7.U) { io.out := (io.a0 >> io.a1(4,0)).asUInt}
    is(8.U) { io.out := io.a0 | io.a1 }
    is(9.U) { io.out := io.a0 & io.a1 }
  }
}
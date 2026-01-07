import chisel3._
import chisel3.util._

class ALU() extends Module{
  val io = IO(new Bundle {
    val func7 = Input(UInt(7.W))
    val func3 = Input(UInt(3.W))
    val a0 = Input(SInt(32.W))
    val a1 = Input(SInt(32.W))
    val out = Output(SInt(32.W))
  })
  val sel = Cat(io.func7, io.func3)
  io.out := 0.S
  switch(sel){
    is("b0000000000".U) { io.out := io.a0 + io.a1 }
    is("b0100000000".U) { io.out := io.a0 - io.a1 }
    is("b0000000001".U) { io.out := io.a0 << io.a1(4,0)}
    is("b0000000010".U) { io.out := (io.a0.asSInt < io.a1.asSInt).asUInt}
    is("b0000000011".U) { io.out := (io.a0 < io.a1).asUInt}
    is("b0000000100".U) { io.out := io.a0 ^ io.a1}
    is("b0000000101".U) { io.out := io.a0 >> io.a1(4,0)}
    is("b0100000101".U) { io.out := (io.a0 >> io.a1(4,0)).asUInt}
    is("b0000000110".U) { io.out := io.a0 | io.a1 }
    is("b0000000111".U) { io.out := io.a0 & io.a1 }

  }
}
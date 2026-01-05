import chisel3._
import chisel3.util._

class ALU() extends Module{
  val io = IO(new Bundle {
    val sel = Input(UInt(2.W))

    val a0 = Input(SInt(32.W))
    val a1 = Input(SInt(32.W))

    val out = Output(SInt(32.W))

  })
  switch(io.sel){
    is("b00".U) { io.out := io.a0 + io.a1 }
    is("b01".U) { io.out := io.a0 - io.a1 }
    is("b10".U) { io.out := io.a0 * io.a1 }
    is("b11".U) { io.out := io.a0 / io.a1 }


  }
}
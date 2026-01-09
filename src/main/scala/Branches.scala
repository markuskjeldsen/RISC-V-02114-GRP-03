import chisel3._
import chisel3.util._

class Branches() extends Module {
  val io = IO(new Bundle {
    val sel  = Input(UInt(3.W)) //funct3
    val a0   = Input(UInt(32.W)) //rs1 value
    val a1   = Input(UInt(32.W)) //rs2 value
    val out = Output(Bool()) //branch taken?
  })

  io.out := false.B

  switch(io.sel) {
    is(0.U) { io.out := (io.a0 === io.a1) } //BEQ
    is(1.U) { io.out := (io.a0 =/= io.a1) }  //BNE
    is(2.U) { io.out := (io.a0.asSInt <  io.a1.asSInt) } //BLT
    is(3.U) { io.out := (io.a0.asSInt >= io.a1.asSInt) } //BGE
    is(4.U) { io.out := (io.a0 <  io.a1) }  //BLTU
    is(5.U) { io.out := (io.a0 >= io.a1) }  //BGEU
  }
}

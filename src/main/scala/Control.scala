import chisel3._
import chisel3.util._

class Control() extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(7.W))
    val func3 = Input(UInt(3.W))
    val func7 = Input(UInt(7.W))
    val ALUsrc = Output(UInt(1.W))
    val ALUctrl = Output(UInt(4.W))
  })

  switch (io.opcode){
    is( "b0010011".U ) {
      io.ALUsrc := 1.U
      switch(io.func3) {
        is("b000".U) {
          io.ALUctrl := 0.U
        }
      }
    }

  }


}

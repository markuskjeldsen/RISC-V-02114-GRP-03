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
    io.ALUsrc := 0.U
    io.ALUctrl := 0.U
  switch (io.opcode){
    is( "b0010011".U ) {
      io.ALUsrc := 1.U
      switch(io.func3) {
        is("b000".U) {
          io.ALUctrl := 0.U
        }
        is("b010".U){
          io.ALUctrl := 3.U
        }
        is("b011".U){
          io.ALUctrl := 4.U
        }
        is("b100".U){
          io.ALUctrl := 5.U
        }
        is("b110".U) {
          io.ALUctrl := 8.U
        }
        is("b111".U){
          io.ALUctrl := 9.U
        }
        is("b001".U){
          io.ALUctrl := 2.U
        }
        is("b101".U) {
          switch(io.func7) {
            is("b0000000".U) {
              io.ALUctrl := 6.U
            }
            is("b0100000".U) {
              io.ALUctrl := 7.U
            }
          }
        }








      }
    }
    is("b0110011".U){
      io.ALUsrc := 0.U
      switch(io.func3){
        is("b000".U) {
          switch(io.func7) {
            is("b0000000".U) {
              io.ALUctrl := 0.U
            }
            is("b0100000".U) {
              io.ALUctrl := 1.U
            }
          }
        }
        is("b001".U) {
          io.ALUctrl := 2.U
        }
        is("b010".U){
          io.ALUctrl := 3.U
        }
        is("b011".U){
          io.ALUctrl := 4.U
        }
        is("b100".U){
          io.ALUctrl := 5.U
        }
        is("b101".U){
          switch(io.func7){
            is("b0000000".U){
              io.ALUctrl := 6.U
            }
            is("b0100000".U){
              io.ALUctrl := 7.U
            }
          }
        }
        is("b110".U){
          io.ALUctrl := 8.U
        }
        is("b111".U){
          io.ALUctrl := 9.U
        }
    is("b0000011".U){ // Memory Load instructions.


    }


      }

    }

  }
}

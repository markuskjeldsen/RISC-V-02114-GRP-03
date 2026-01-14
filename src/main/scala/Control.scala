import chisel3._
import chisel3.util._

class Control() extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(7.W))
    val func3 = Input(UInt(3.W))
    val func7 = Input(UInt(7.W))
    val ALUsrc = Output(UInt(1.W))
    val ALUctrl = Output(UInt(4.W))
    val BranchCtrl = UInt(3.W)
    val regWrite = Bool()
  })
    io.ALUsrc := 0.U
    io.ALUctrl := 0.U
    io.BranchCtrl := 0.U
  io.regWrite := MuxLookup(io.opcode, false.B, Seq(
    "b0110111".U -> true.B, // LUI
    "b0010111".U -> true.B, // AUIPC
    "b1101111".U -> true.B, // JAL
    "b1100111".U -> true.B, // JALR
    "b0000011".U -> true.B, // LOAD
    "b0010011".U -> true.B, // ALU imm
    "b0110011".U -> true.B  // ALU reg
  ))

  switch (io.opcode){
    is( "b0010011".U ) { // Integer register-immediate instructions
      io.ALUsrc := 1.U
      switch(io.func3) {
        is("b000".U){io.ALUctrl := 0.U} // add immediate
        is("b010".U){io.ALUctrl := 3.U} // set less than immediate
        is("b011".U){io.ALUctrl := 4.U} // set less than immed unsgn
        is("b100".U){io.ALUctrl := 5.U} // xor immediate
        is("b110".U){io.ALUctrl := 8.U} // or immediate
        is("b111".U){io.ALUctrl := 9.U} // and immediate
        is("b001".U){io.ALUctrl := 2.U} // constant-shift left
        is("b101".U){ switch(io.func7) {
            is("b0000000".U) {io.ALUctrl := 6.U} // constant-shift right
            is("b0100000".U) {io.ALUctrl := 7.U} // constant-shift right arithmetic
        }}
      }
    }
    is("b0110011".U){ // Integer register-register instructions
      io.ALUsrc := 0.U
      switch(io.func3){
        is("b000".U) { switch(io.func7) {
            is("b0000000".U) {io.ALUctrl := 0.U} // add
            is("b0100000".U) {io.ALUctrl := 1.U} // subtract
        }}
        is("b001".U){io.ALUctrl := 2.U} // register-shift left
        is("b010".U){io.ALUctrl := 3.U} // set less than
        is("b011".U){io.ALUctrl := 4.U} // set less than unsigned
        is("b100".U){io.ALUctrl := 5.U} // xor
        is("b101".U){switch(io.func7){
            is("b0000000".U){io.ALUctrl := 6.U} // register-shift right (logical)
            is("b0100000".U){io.ALUctrl := 7.U} // register-shift right (arithmetic)
        }}
        is("b110".U){io.ALUctrl := 8.U} // or
        is("b111".U){io.ALUctrl := 9.U} // and
      }
    }
    is("b0100011".U) { // Memory Store Instructions
      io.ALUsrc := 1.U // use imm
    }
    is("b0000011".U) { // Memory Load Instructions
      io.ALUsrc := 1.U // use imm
    }

    is("b1100011".U){ // Branches configuration
      io.ALUsrc := 0.U
      switch(io.func3) {
        is("b000".U){io.BranchCtrl := 0.U} // BEQ
        is("b001".U){io.BranchCtrl := 1.U} // BNE
        is("b100".U){io.BranchCtrl := 2.U} // BLT
        is("b101".U){io.BranchCtrl := 3.U} // BGE
        is("b110".U){io.BranchCtrl := 4.U} // BLTU
        is("b111".U){io.BranchCtrl := 5.U} // BGEU
      }
    }

    is("b0110111".U) { // LUI
      io.ALUsrc  := 1.U
      io.ALUctrl := 0.U // ADD
    }

    is("b0010111".U) { // AUIPC
      io.ALUsrc  := 1.U
      io.ALUctrl := 0.U // ADD
    }


  } // end opcode switch
}

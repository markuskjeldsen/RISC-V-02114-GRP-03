import chisel3._
import chisel3.util._

class Control() extends Module {
  val io = IO(new Bundle {
    val opcode     = Input(UInt(7.W))
    val func3      = Input(UInt(3.W))
    val func7      = Input(UInt(7.W))

    val ALUsrc     = Output(UInt(1.W))
    val ALUctrl    = Output(UInt(4.W))
    val BranchCtrl = Output(UInt(3.W))

    val regWrite   = Output(Bool())
    val loadedData = Output(Bool())
  })

  io.ALUsrc     := 0.U
  io.ALUctrl    := 0.U
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

  io.loadedData := (io.opcode === "b0000011".U)

  switch(io.opcode) {
    is("b0010011".U) { // I-type ALU
      io.ALUsrc := 1.U
      switch(io.func3) {
        is("b000".U) { io.ALUctrl := 0.U } // ADDI
        is("b010".U) { io.ALUctrl := 3.U } // SLTI
        is("b011".U) { io.ALUctrl := 4.U } // SLTIU
        is("b100".U) { io.ALUctrl := 5.U } // XORI
        is("b110".U) { io.ALUctrl := 8.U } // ORI
        is("b111".U) { io.ALUctrl := 9.U } // ANDI
        is("b001".U) { io.ALUctrl := 2.U } // SLLI
        is("b101".U) {
          switch(io.func7) {
            is("b0000000".U) { io.ALUctrl := 6.U } // SRLI
            is("b0100000".U) { io.ALUctrl := 7.U } // SRAI
          }
        }
      }
    }

    is("b0110011".U) { // R-type
      io.ALUsrc := 0.U
      switch(io.func3) {
        is("b000".U) {
          switch(io.func7) {
            is("b0000000".U) { io.ALUctrl := 0.U } // ADD
            is("b0100000".U) { io.ALUctrl := 1.U } // SUB
          }
        }
        is("b001".U) { io.ALUctrl := 2.U } // SLL
        is("b010".U) { io.ALUctrl := 3.U } // SLT
        is("b011".U) { io.ALUctrl := 4.U } // SLTU
        is("b100".U) { io.ALUctrl := 5.U } // XOR
        is("b101".U) {
          switch(io.func7) {
            is("b0000000".U) { io.ALUctrl := 6.U } // SRL
            is("b0100000".U) { io.ALUctrl := 7.U } // SRA
          }
        }
        is("b110".U) { io.ALUctrl := 8.U } // OR
        is("b111".U) { io.ALUctrl := 9.U } // AND
      }
    }

    is("b0100011".U) { // Store
      io.ALUsrc  := 1.U
      io.ALUctrl := 0.U
    }

    is("b0000011".U) { // Load
      io.ALUsrc  := 1.U
      io.ALUctrl := 0.U
    }

    is("b1100011".U) { // Branch
      io.ALUsrc := 0.U
      switch(io.func3) {
        is("b000".U) { io.BranchCtrl := 0.U } // BEQ
        is("b001".U) { io.BranchCtrl := 1.U } // BNE
        is("b100".U) { io.BranchCtrl := 2.U } // BLT
        is("b101".U) { io.BranchCtrl := 3.U } // BGE
        is("b110".U) { io.BranchCtrl := 4.U } // BLTU
        is("b111".U) { io.BranchCtrl := 5.U } // BGEU
      }
    }

    is("b0110111".U) { // LUI
      io.ALUsrc  := 1.U
      io.ALUctrl := 0.U
    }

    is("b0010111".U) {
      io.ALUsrc  := 1.U
      io.ALUctrl := 0.U
    }
  }
}

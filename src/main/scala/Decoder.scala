import chisel3._
import chisel3.util._

class Decoder() extends Module {
  val io = IO(new Bundle {
    val input  = Input(UInt(32.W))
    val opcode = Output(UInt(7.W))
    val rd     = Output(UInt(5.W))
    val func3  = Output(UInt(3.W))
    val rs1    = Output(UInt(5.W))
    val imm    = Output(UInt(32.W))

    val func7  = Output(UInt(7.W))
    val rs2    = Output(UInt(5.W))
    val shamt  = Output(UInt(5.W))
  })

  // Default assignments
  io.opcode := io.input(6, 0)
  io.rd     := 0.U
  io.func3  := 0.U
  io.rs1    := 0.U
  io.imm    := 0.U
  io.func7  := 0.U
  io.rs2    := 0.U
  io.shamt  := 0.U

  switch(io.opcode) {
    is("b0010011".U) { // I-type Integer Instructions
      io.func3 := io.input(14, 12)
      switch(io.func3) {
        is("b000".U) { // ADDI
          io.rd  := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          val rawImm = io.input(31, 20)
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b010".U) { // SLTI
          io.rd  := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          val rawImm = io.input(31, 20)
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b011".U) { // SLTIU
          io.rd  := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          val rawImm = io.input(31, 20)
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b100".U) { // XORI
          io.rd  := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          val rawImm = io.input(31, 20)
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b110".U) { // ORI
          io.rd  := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          val rawImm = io.input(31, 20)
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b111".U) { // ANDI
          io.rd  := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          val rawImm = io.input(31, 20)
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b001".U) { // SLLI
          io.rd    := io.input(11, 7)
          io.rs1   := io.input(19, 15)
          io.shamt := io.input(24, 20)
          io.func7 := io.input(31, 25)
        }
        is("b101".U) { // SRLI / SRAI
          io.func7 := io.input(31, 25)
          switch(io.func7) {
            is("b0000000".U) { // SRLI
              io.rd    := io.input(11, 7)
              io.rs1   := io.input(19, 15)
              io.shamt := io.input(24, 20)
            }
            is("b0100000".U) { // SRAI
              io.rd    := io.input(11, 7)
              io.rs1   := io.input(19, 15)
              io.shamt := io.input(24, 20)
            }
          }
        }
      }
    }

    is("b0110011".U) { // R-type Instructions
      io.func3 := io.input(14, 12)
      io.func7 := io.input(31, 25)
      io.rd    := io.input(11, 7)
      io.rs1   := io.input(19, 15)
      io.rs2   := io.input(24, 20)
    }

    is("b0000011".U) { // Load Instructions
      io.func3 := io.input(14, 12)
      io.rs1   := io.input(19, 15)
      io.rd    := io.input(11, 7)
      val rawImm = io.input(31, 20)
      io.imm   := rawImm.asSInt.pad(32).asUInt
    }

    is("b0100011".U) { // Store Instructions
      io.func3 := io.input(14, 12)
      io.rs1   := io.input(19, 15)
      io.rs2   := io.input(24, 20)
      // S-type immediate: [31:25] as MSB and [11:7] as LSB
      val rawImm = Cat(io.input(31, 25), io.input(11, 7))
      io.imm   := rawImm.asSInt.pad(32).asUInt
    }

    is("b1100011".U) { // Branch instructions
      io.rs2 := io.input(24,20)
      io.rs1 := io.input(19,15)
      io.func3 := io.input(14,12)

      // 1. Extract bits according to spec
      // 2. Append a "0".U constant to the end (bit 0)
      val rawImm = Cat(
        io.input(31),     // imm[12]
        io.input(7),      // imm[11]
        io.input(30, 25), // imm[10:5]
        io.input(11, 8),  // imm[4:1]
        0.U(1.W)          // imm[0] (Always zero)
      )

      // 3. Sign extend from the 13th bit (bit index 12) to 32 bits
      io.imm := rawImm.asSInt.pad(32).asUInt
    }
    is("b0110111".U){//LUI instruction
      io.rd := io.input(11,7)
      io.imm := Cat(io.input(31,12),0.U(12.W))
    }
    is("b0010111".U){ //AUIPC intruction
      io.rd := io.input(11,7)
      io.imm := Cat(io.input(31,12),0.U(12.W))
    }
    is("b1101111".U) { // JAL (Jump and Link)
      // rd is at io.input(11, 7) - ensure your decoder handles this
      io.rd := io.input(11,7)

      val rawImm = Cat(
        io.input(31),        // imm[20]
        io.input(19, 12),    // imm[19:12]
        io.input(20),        // imm[11]
        io.input(30, 21),    // imm[10:1]
        0.U(1.W)             // imm[0] (Implicit zero)
      )

      // Sign extend from the 21st bit to 32 bits
      io.imm := rawImm.asSInt.pad(32).asUInt
    }

    is("b1100111".U) { // JALR
      io.rd    := io.input(11, 7)
      io.func3 := io.input(14, 12)
      io.rs1   := io.input(19, 15)

      // I-type immediate (12 bits: 31 down to 20)
      // Must be sign-extended to 32 bits!
      val rawImm = io.input(31, 20)
      io.imm := rawImm.asSInt.pad(32).asUInt
    }


  }
}

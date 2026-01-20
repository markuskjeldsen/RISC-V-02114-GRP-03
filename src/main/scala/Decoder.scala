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

  io.opcode := io.input(6, 0)
  io.rd     := 0.U
  io.func3  := 0.U
  io.rs1    := 0.U
  io.imm    := 0.U
  io.func7  := 0.U
  io.rs2    := 0.U
  io.shamt  := 0.U

  switch(io.opcode) {

    is("b0010011".U) { // I-type ALU
      io.func3 := io.input(14, 12)
      io.rd    := io.input(11, 7)
      io.rs1   := io.input(19, 15)
      io.func7 := io.input(31, 25)
      io.shamt := io.input(24, 20)

      val rawImm = io.input(31, 20)
      io.imm := rawImm.asSInt.pad(32).asUInt
    }

    is("b0110011".U) { // R-type
      io.func3 := io.input(14, 12)
      io.func7 := io.input(31, 25)
      io.rd    := io.input(11, 7)
      io.rs1   := io.input(19, 15)
      io.rs2   := io.input(24, 20)
    }

    is("b0000011".U) { // Load (I-type)
      io.func3 := io.input(14, 12)
      io.rs1   := io.input(19, 15)
      io.rd    := io.input(11, 7)

      val rawImm = io.input(31, 20)
      io.imm := rawImm.asSInt.pad(32).asUInt
    }

    is("b0100011".U) { // Store (S-type)
      io.func3 := io.input(14, 12)
      io.rs1   := io.input(19, 15)
      io.rs2   := io.input(24, 20)

      val rawImm = Cat(io.input(31, 25), io.input(11, 7))
      io.imm := rawImm.asSInt.pad(32).asUInt
    }

    is("b1100011".U) { // Branch (B-type)  ***FIXED (imm[0]=0)***
      io.rs1   := io.input(19, 15)
      io.rs2   := io.input(24, 20)
      io.func3 := io.input(14, 12)

      val rawImm = Cat(
        io.input(31),     // imm[12]
        io.input(7),      // imm[11]
        io.input(30, 25), // imm[10:5]
        io.input(11, 8),  // imm[4:1]
        0.U(1.W)          // imm[0]
      )
      io.imm := rawImm.asSInt.pad(32).asUInt
    }

    is("b0110111".U) { // LUI (U-type)
      io.rd  := io.input(11, 7)
      io.imm := Cat(io.input(31, 12), 0.U(12.W))
    }

    is("b0010111".U) { // AUIPC (U-type)
      io.rd  := io.input(11, 7)
      io.imm := Cat(io.input(31, 12), 0.U(12.W))
    }

    is("b1101111".U) { // JAL (J-type) ***FIXED***
      io.rd := io.input(11, 7)

      val rawImm = Cat(
        io.input(31),     // imm[20]
        io.input(19, 12), // imm[19:12]
        io.input(20),     // imm[11]
        io.input(30, 21), // imm[10:1]
        0.U(1.W)          // imm[0]
      )
      io.imm := rawImm.asSInt.pad(32).asUInt
    }

    is("b1100111".U) { // JALR (I-type) ***FIXED sign-ext***
      io.rd    := io.input(11, 7)
      io.func3 := io.input(14, 12)
      io.rs1   := io.input(19, 15)

      val rawImm = io.input(31, 20)
      io.imm := rawImm.asSInt.pad(32).asUInt
    }
  }
}

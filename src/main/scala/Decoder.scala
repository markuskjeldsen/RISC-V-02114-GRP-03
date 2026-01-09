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

    val func7 = Output(UInt(7.W))
    val rs2 = Output(UInt(5.W))
    val shamt = Output(UInt(5.W))
  })
  io.opcode := io.input(6,0)
  io.rd     := 0.U
  io.func3  := 0.U
  io.rs1    := 0.U
  io.imm    := 0.U
  io.func7 := 0.U
  io.rs2 := 0.U
  io.shamt := 0.U
  //val rawImm = 0.U
  switch(io.opcode){
    is("b0010011".U){ //intger functions
      io.func3  := io.input(14,12)
      switch(io.func3){
        is("b000".U) { //ADDI
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          // currently this is UInt(12.W)
          val rawImm = io.input(31, 20)
          // 3. Convert to UInt.
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b010".U) { //SLTI
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          // currently this is UInt(12.W)
          val rawImm = io.input(31, 20)
          // 3. Convert to UInt.
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b011".U) { //SLTIU
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          // currently this is UInt(12.W)
          val rawImm = io.input(31, 20)
          // 3. Convert to UInt.
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b100".U) { //XORI
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          // currently this is UInt(12.W)
          val rawImm = io.input(31, 20)
          // 3. Convert to UInt.
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b110".U) { //ORI
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          // currently this is UInt(12.W)
          val rawImm = io.input(31, 20)
          // 3. Convert to UInt.
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b111".U) { //ANDI
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          // currently this is UInt(12.W)
          val rawImm = io.input(31, 20)
          // 3. Convert to UInt.
          io.imm := rawImm.asSInt.pad(32).asUInt
        }
        is("b001".U) { //SLLI
          io.rd := io.input(11, 7)
          io.rs1 := io.input(19, 15)
          io.shamt := io.input(24,20)
          io.func7 := io.input(31,25)

        }
        is("b101".U) {
          io.func7 := io.input(31,25)
          switch(io.func7) {


            is("b0000000".U) { //SRLI
            io.rd := io.input(11, 7)
            io.rs1 := io.input(19, 15)
            io.shamt := io.input(24, 20)
          }
            is("b0100000".U) { //SRAI
              io.rd := io.input(11, 7)
              io.rs1 := io.input(19, 15)
              io.shamt := io.input(24, 20)
            }
          }
        }
      }
    }
    is("b0110011".U){
      io.func3 := io.input(14,12)
      io.func7 := io.input(31,25)
      io.rd := io.input(11,7)
      io.rs1:= io.input(19,15)
      io.rs2 := io.input(24,20)
          }
    }
    is("b0000011".U){
      io.func3 := io.input(14,12)
      io.imm := io.input(31,20)
      io.rs1 := io.input(19,15)
      io.rd := io.input(11,7)
    }

    is("b0100011".U){
      io.func3 := io.input(14,12)
      io.imm := io.input(31,25)
      io.rs1 := io.input(19,15)
      io.rs2 := io.input(24,20)
      io.imm := io.input(11,7)
    }
  }
  //val opcode = io.input(6,0)
  //val rd     = io.input(11,7)
  //val func3  = io.input(14,12)
  //val rs1    = io.input(19,15)

  // currently this is UInt(12.W)
  //val rawImm = io.input(31, 20)

  //io.opcode := opcode
  //io.rd     := rd
  //io.func3  := func3
  //io.rs1    := rs1

  // 3. Convert to UInt.
  //io.imm := rawImm.asSInt.pad(32).asUInt


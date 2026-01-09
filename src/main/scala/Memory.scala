import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class Memory( ProgPath: String, instMemWords: Int = 4096, dataMemWords: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val instAddr = Input(UInt(32.W))
    val inst = Output(UInt(32.W))

    // Data memory
    val dataAddr = Input(UInt(32.W))
    val rs2Data = Input(UInt(32.W))
    val opcode = Input(UInt(7.W))

    // RV32I load/store control
    val func3 = Input(UInt(3.W)) // 00=byte, 01=half, 10=word

    val readData = Output(UInt(32.W))
  })
  val memSign = io.func3(2)
  // Instruction Memory
  val iMem = SyncReadMem(instMemWords, UInt(32.W))

  loadMemoryFromFile(iMem, ProgPath)

  val instWordAddr = io.instAddr(31, 2)

  val instReg = RegInit(0.U(32.W))
  instReg := iMem.read(instWordAddr, true.B)

  io.inst := instReg

  // Data Memory (DMEM)
  val dMem = SyncReadMem(dataMemWords, Vec(4, UInt(8.W)))

  val wordAddr = io.dataAddr(31, 2)
  val byteOffset = io.dataAddr(1, 0)


  // STORE LOGIC (SB / SH / SW)
  val writeMask = Wire(Vec(4, Bool()))
  writeMask := VecInit(Seq.fill(4)(false.B))



  val writeBytes = io.rs2Data.asTypeOf(Vec(4, UInt(8.W)))

  // LOAD LOGIC (LB/LBU/LH/LHU/LW)
  val readBytes = dMem.read(wordAddr, io.opcode === "b0000011".U)
  val readReg = Reg(Vec(4, UInt(8.W)))


  val loadData = Wire(UInt(32.W))
  loadData := 0.U
  switch(io.opcode) {
    is("b0000011".U) {
      switch(io.func3) {
        is("b000".U) { // LB / LBU
          val byte = readReg(byteOffset)
          loadData := Mux(memSign, Cat(Fill(24, byte(7)), byte), Cat(0.U(24.W), byte))
          readReg := readBytes
        }
        is("b100".U) { // LB / LBU
          val byte = readReg(byteOffset)
          loadData := Mux(memSign, Cat(Fill(24, byte(7)), byte), Cat(0.U(24.W), byte))
          readReg := readBytes
        }

        is("b001".U) { // LH / LHU
          val half = Cat(readReg(byteOffset + 1.U), readReg(byteOffset))
          // LH, LHU
          loadData := Mux(memSign, Cat(Fill(16, half(15)), half), Cat(0.U(16.W), half))
          readReg := readBytes
        }
        is("b101".U) { // LH / LHU
          val half = Cat(readReg(byteOffset + 1.U), readReg(byteOffset))
          // LH, LHU
          loadData := Mux(memSign, Cat(Fill(16, half(15)), half), Cat(0.U(16.W), half))
          readReg := readBytes
        }
        // LW
        is("b010".U) { // LW
          loadData := Cat(readReg(3), readReg(2), readReg(1), readReg(0))
          readReg := readBytes
        }
      }
  }
    is("b0100011".U){
      switch(io.func3) {
        is("b000".U) { // SB
          writeMask(byteOffset) := true.B
          dMem.write(wordAddr, writeBytes, writeMask)

        }
        // SH
        is("b001".U) {
          writeMask(byteOffset) := true.B
          writeMask(byteOffset + 1.U) := true.B
          dMem.write(wordAddr, writeBytes, writeMask)

        }
        // SW
        is("b010".U) {
          writeMask := VecInit(Seq.fill(4)(true.B))
          dMem.write(wordAddr, writeBytes, writeMask)
        }
      }
    }
}

  io.readData := loadData
}

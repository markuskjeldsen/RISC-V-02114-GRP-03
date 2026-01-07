import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile


class Memory( ProgPath: String, instMemWords: Int = 4096, dataMemWords: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val instAddr = Input(UInt(32.W))
    val inst     = Output(UInt(32.W))

    // Data memory
    val dataAddr  = Input(UInt(32.W))
    val writeData = Input(UInt(32.W))
    val memRead   = Input(Bool())
    val memWrite  = Input(Bool())

    // RV32I load/store control
    val memSize = Input(UInt(2.W)) // 00=byte, 01=half, 10=word
    val memSign = Input(Bool())    // 1=signed, 0=unsigned (loads only)

    val readData = Output(UInt(32.W))
  })

  // Instruction Memory
  val iMem = SyncReadMem(instMemWords, UInt(32.W))

  loadMemoryFromFile(iMem, ProgPath)

  val instWordAddr = io.instAddr(31, 2)

  val instReg = RegInit(0.U(32.W))
  instReg := iMem.read(instWordAddr, true.B)

  io.inst := instReg

  // Data Memory (DMEM)
  val dMem = SyncReadMem(dataMemWords, Vec(4, UInt(8.W)))

  val wordAddr   = io.dataAddr(31, 2)
  val byteOffset = io.dataAddr(1, 0)


  // STORE LOGIC (SB / SH / SW)
  val writeMask = Wire(Vec(4, Bool()))
  writeMask := VecInit(Seq.fill(4)(false.B))

  switch(io.memSize) {
    is("b00".U) { // SB
      writeMask(byteOffset) := true.B
    }
    is("b01".U) { // SH
      writeMask(byteOffset)       := true.B
      writeMask(byteOffset + 1.U) := true.B
    }
    is("b10".U) { // SW
      writeMask := VecInit(Seq.fill(4)(true.B))
    }
  }

  val writeBytes = io.writeData.asTypeOf(Vec(4, UInt(8.W)))

  when(io.memWrite) {
    dMem.write(wordAddr, writeBytes, writeMask)
  }

  // LOAD LOGIC (LB/LBU/LH/LHU/LW)
  val readBytes = dMem.read(wordAddr, io.memRead)

  val readReg = Reg(Vec(4, UInt(8.W)))
  when(io.memRead) {
    readReg := readBytes
  }

  val loadData = Wire(UInt(32.W))
  loadData := 0.U

  switch(io.memSize) {
    is("b00".U) { // LB / LBU
      val byte = readReg(byteOffset)
      loadData := Mux(
        io.memSign,
        Cat(Fill(24, byte(7)), byte), // LB
        Cat(0.U(24.W), byte)          // LBU
      )
    }

    is("b01".U) { // LH / LHU
      val half = Cat(
        readReg(byteOffset + 1.U),
        readReg(byteOffset)
      )
      loadData := Mux(
        io.memSign,
        Cat(Fill(16, half(15)), half), // LH
        Cat(0.U(16.W), half)           // LHU
      )
    }

    is("b10".U) { // LW
      loadData := Cat(
        readReg(3),
        readReg(2),
        readReg(1),
        readReg(0)
      )
    }
  }

  io.readData := loadData

  // Optional RV32I alignment checks
  when(io.memSize === "b01".U) { // halfword
    assert(io.dataAddr(0) === 0.U)
  }
  when(io.memSize === "b10".U) { // word
    assert(io.dataAddr(1, 0) === 0.U)
  }
}

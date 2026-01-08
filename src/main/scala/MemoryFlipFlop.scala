import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFileInline


class MemoryFlipFlop(ProgPath: String, instMemWords: Int = 20, dataMemWords: Int = 20) extends Module {

  val io = IO(new Bundle {
    val instAddr = Input(UInt(32.W))
    val inst     = Output(UInt(32.W))

    // Data memory
    val dataAddr  = Input(UInt(32.W))
    val writeData = Input(UInt(32.W))
    val memRead   = Input(Bool())
    val memWrite  = Input(Bool())

    val memSize = Input(UInt(2.W)) // 00=byte, 01=half, 10=word
    val memSign = Input(Bool())

    val readData = Output(UInt(32.W))
  })
  val iMemInit = Mem(instMemWords, UInt(32.W))
  loadMemoryFromFileInline(iMemInit, ProgPath)

  // Flip-flop memory
  val iMem = Reg(Vec(instMemWords, UInt(32.W)))

  // Copy at reset
  when (reset.asBool) {
    for (i <- 0 until instMemWords) {
      iMem(i) := iMemInit(i)
    }
  }

  val instWordAddr = io.instAddr(31, 2)
  io.inst := iMem(instWordAddr)


  val dMem = RegInit(VecInit(Seq.fill(dataMemWords)(VecInit(Seq.fill(4)(0.U(8.W))))))

  val wordAddr   = io.dataAddr(31, 2)
  val byteOffset = io.dataAddr(1, 0)

  val writeBytes = io.writeData.asTypeOf(Vec(4, UInt(8.W)))

  when(io.memWrite) {
    switch(io.memSize) {
      is("b00".U) { // SB
        dMem(wordAddr)(byteOffset) := writeBytes(0)
      }
      is("b01".U) { // SH
        dMem(wordAddr)(byteOffset)       := writeBytes(0)
        dMem(wordAddr)(byteOffset + 1.U) := writeBytes(1)
      }
      is("b10".U) { // SW
        dMem(wordAddr) := writeBytes
      }
    }
  }

  val readBytes = dMem(wordAddr)

  val loadData = Wire(UInt(32.W))
  loadData := 0.U

  when(io.memRead) {
    switch(io.memSize) {
      is("b00".U) { // LB / LBU
        val b = readBytes(byteOffset)
        loadData := Mux(
          io.memSign,
          Cat(Fill(24, b(7)), b),
          Cat(0.U(24.W), b)
        )
      }

      is("b01".U) { // LH / LHU
        val h = Cat(readBytes(byteOffset + 1.U), readBytes(byteOffset))
        loadData := Mux(
          io.memSign,
          Cat(Fill(16, h(15)), h),
          Cat(0.U(16.W), h)
        )
      }

      is("b10".U) { // LW
        loadData := Cat(
          readBytes(3),
          readBytes(2),
          readBytes(1),
          readBytes(0)
        )
      }
    }
  }

  io.readData := loadData

  // Alignment checks
  when(io.memSize === "b01".U) {
    assert(io.dataAddr(0) === 0.U)
  }
  when(io.memSize === "b10".U) {
    assert(io.dataAddr(1, 0) === 0.U)
  }
}
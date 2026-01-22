import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class Memory(ProgPath: String, instMemWords: Int = 4096, dataMemWords: Int = 4096) extends Module {
  val io = IO(new Bundle {
    // Instruction port
    val instAddr  = Input(UInt(32.W))
    val instClear = Input(Bool()) // kept for compatibility; flush in IF/ID
    val inst      = Output(UInt(32.W))

    // Data port
    val dataAddr = Input(UInt(32.W))
    val rs2Data  = Input(UInt(32.W))
    val opcode   = Input(UInt(7.W))
    val func3    = Input(UInt(3.W))

    val readData = Output(UInt(32.W))
  })

  // ---------------------------
  // Instruction memory (SYNC)  <-- KEEP THIS SYNC for your CPU fetch alignment
  // ---------------------------
  //val iMem = SyncReadMem(instMemWords, UInt(32.W))
  //loadMemoryFromFile(iMem, ProgPath)

  // 1-cycle latency instruction fetch (CPU already compensates with fetchPCReg)
  //io.inst := iMem.read(io.instAddr(31, 2), true.B)
val array = new Array[Int](17)
  array(0) = 0x10000113
  array(1) = 0x7ff00293
  array(2) = 0x00e29293
  array(3) = 0x00000313
  array(4) = 0x00100393
  array(5) = 0x00000413
  array(6) = 0x01000493
  array(7) = 0x00628863
  array(8) = 0x00130313
  array(9) = 0x00000013
  array(10) = 0xfe000ae3
  array(11) = 0x00000313
  array(12) = 0x00139393
  array(13) = 0x00140413
  array(14) = 0x00940463
  array(15) = 0xfe0000e3
  array(16) = 0x00138393
  array(17) = 0xfc000ce3


  val instructionMem = VecInit(array.toIndexedSeq.map(_.S(32.W).asUInt))
  io.inst := instructionMem(io.instAddr(31,2))



  // ---------------------------
  // Data memory (COMBINATIONAL READ for 5-stage)
  // ---------------------------
  //
  // Using SyncReadMem here makes load data arrive next cycle (acts like MEM2),
  // which breaks a strict 5-stage design unless you add an extra stage.
  //
  val dMem = Mem(dataMemWords, Vec(4, UInt(8.W)))

  val wordAddr   = io.dataAddr(31, 2)
  val byteOffset = io.dataAddr(1, 0)

  val isLoad  = (io.opcode === "b0000011".U)
  val isStore = (io.opcode === "b0100011".U)

  // Alignment checks (if you are not implementing misaligned accesses)
  when(isStore && io.func3 === "b001".U) { // SH
    assert(io.dataAddr(0) === 0.U, "SH misaligned address")
  }
  when(isStore && io.func3 === "b010".U) { // SW
    assert(io.dataAddr(1, 0) === 0.U, "SW misaligned address")
  }
  when(isLoad && io.func3 === "b001".U) { // LH/LHU
    assert(io.dataAddr(0) === 0.U, "LH/LHU misaligned address")
  }
  when(isLoad && io.func3 === "b010".U) { // LW
    assert(io.dataAddr(1, 0) === 0.U, "LW misaligned address")
  }

  // ---------- STORE ----------
  // Little-endian byte lanes from rs2Data
  val writeBytes = Wire(Vec(4, UInt(8.W)))
  writeBytes(0) := io.rs2Data(7, 0)
  writeBytes(1) := io.rs2Data(15, 8)
  writeBytes(2) := io.rs2Data(23, 16)
  writeBytes(3) := io.rs2Data(31, 24)

  // Safe write mask (avoid byteOffset+1 overflow by relying on alignment asserts)
  val writeMask = WireDefault(VecInit(Seq.fill(4)(false.B)))
  switch(io.func3) {
    is("b000".U) { // SB
      writeMask(byteOffset) := true.B
    }
    is("b001".U) { // SH (aligned => byteOffset is 0 or 2)
      writeMask(byteOffset) := true.B
      writeMask(byteOffset + 1.U) := true.B
    }
    is("b010".U) { // SW
      writeMask := VecInit(Seq.fill(4)(true.B))
    }
  }

  when(isStore) {
    dMem.write(wordAddr, writeBytes, writeMask)
  }

  // ---------- LOAD ----------
  // Combinational read for 5-stage
  val rawReadBytes = dMem.read(wordAddr)

  val b = rawReadBytes(byteOffset)
  val h = Cat(rawReadBytes(byteOffset + 1.U), rawReadBytes(byteOffset))
  val w = Cat(rawReadBytes(3), rawReadBytes(2), rawReadBytes(1), rawReadBytes(0))

  val loadData = WireDefault(0.U(32.W))
  switch(io.func3) {
    is("b000".U) { loadData := b.asSInt.pad(32).asUInt }        // LB
    is("b001".U) { loadData := h.asSInt.pad(32).asUInt }        // LH
    is("b010".U) { loadData := w }                              // LW
    is("b100".U) { loadData := Cat(0.U(24.W), b) }              // LBU
    is("b101".U) { loadData := Cat(0.U(16.W), h) }              // LHU
  }

  io.readData := Mux(isLoad, loadData, 0.U)
}

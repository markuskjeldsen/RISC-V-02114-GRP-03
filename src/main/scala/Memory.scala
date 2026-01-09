import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile

class Memory(ProgPath: String, instMemWords: Int = 4096, dataMemWords: Int = 4096) extends Module {
  val io = IO(new Bundle {
    val instAddr = Input(UInt(32.W))
    val inst     = Output(UInt(32.W))

    // Data memory
    val dataAddr = Input(UInt(32.W))
    val rs2Data  = Input(UInt(32.W))
    val opcode   = Input(UInt(7.W))
    val func3    = Input(UInt(3.W))

    val readData = Output(UInt(32.W))
  })

  // --- Instruction Memory ---
  // SyncReadMem acts like a register: Address in @ T, Data out @ T+1
  val iMem = SyncReadMem(instMemWords, UInt(32.W))
  loadMemoryFromFile(iMem, ProgPath)

  // We don't need an extra Reg here if we want 1-cycle latency
  // instAddr -> iMem -> io.inst (available next cycle)
  io.inst := iMem.read(io.instAddr(31, 2), true.B)

  // --- Data Memory (DMEM) ---
  val dMem = SyncReadMem(dataMemWords, Vec(4, UInt(8.W)))

  val wordAddr   = io.dataAddr(31, 2)
  val byteOffset = io.dataAddr(1, 0)

  // Detect Operation Types
  val isLoad  = io.opcode === "b0000011".U
  val isStore = io.opcode === "b0100011".U

  // --- STORE LOGIC ---
  val writeMask  = WireDefault(VecInit(Seq.fill(4)(false.B)))
  val writeBytes = io.rs2Data.asTypeOf(Vec(4, UInt(8.W)))

  switch(io.func3) {
    is("b000".U) { writeMask(byteOffset) := true.B } // SB
    is("b001".U) { // SH
      writeMask(byteOffset) := true.B
      writeMask(byteOffset + 1.U) := true.B
    }
    is("b010".U) { writeMask := VecInit(Seq.fill(4)(true.B)) } // SW
  }

  when(isStore) {
    dMem.write(wordAddr, writeBytes, writeMask)
  }

  // --- LOAD LOGIC ---
  // 1. Send read command to memory
  val rawReadBytes = dMem.read(wordAddr, isLoad)

  // 2. Register the control signals to "match" the 1-cycle memory latency
  val offReg   = RegNext(byteOffset)
  val func3Reg = RegNext(io.func3)

  // 3. Process the data (Comb logic based on registered signals)
  val selectedByte = rawReadBytes(offReg)
  val selectedHalf = Cat(rawReadBytes(offReg + 1.U), rawReadBytes(offReg))
  val selectedWord = rawReadBytes.asUInt

  val loadData = WireDefault(0.U(32.W))
  switch(func3Reg) {
    is("b000".U) { loadData := selectedByte.asSInt.asUInt }          // LB (Sign extended)
    is("b001".U) { loadData := selectedHalf.asSInt.asUInt }          // LH (Sign extended)
    is("b010".U) { loadData := selectedWord }                       // LW
    is("b100".U) { loadData := Cat(0.U(24.W), selectedByte) }        // LBU
    is("b101".U) { loadData := Cat(0.U(16.W), selectedHalf) }        // LHU
  }

  io.readData := loadData
}

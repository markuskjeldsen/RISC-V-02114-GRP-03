import chisel3.{RegInit, dontTouch, _}
import chisel3.util._

// Import your custom package
import pipelineregisters._


// FETCH // DECODE // EXECUTE // MEMORY // WRITEBACK

class CPU(ProgPath: String) extends Module {
  val io = IO(new Bundle {
    val PRGCNT = Input(UInt(32.W))
  })
  val decoder = Module(new Decoder())


  val PC = RegInit(0.U(32.W))
  val PCMuxSel = RegInit(0.U(2.W))

  PC := MuxCase(0.U, Seq(
    // ALU & Immediate operations use the ALU result
    (PCMuxSel === 0.U) -> (PC + 4.U), // this is the normal operation
    (PCMuxSel === 1.U) -> PC,     // dont increment
    (PCMuxSel === 2.U) -> (decoder.io.imm << 1) //
  ))


  val ProgMem = Module(new Memory(ProgPath))
  ProgMem.io.writeData := 0.U
  ProgMem.io.dataAddr := 0.U
  ProgMem.io.memSize := 0.U
  ProgMem.io.memSign := 0.U
  ProgMem.io.memRead := 0.U
  ProgMem.io.memWrite := 0.U



  // --- FETCH STAGE ---
  ProgMem.io.instAddr := PC
  val current_instr = ProgMem.io.inst
  val current_pc    = PC



  // --- IF/ID PIPELINE REGISTER --------------------------------------------------------
  val IFID_reg = RegInit(0.U.asTypeOf(new IFIDBundle))
  dontTouch(IFID_reg)

  // Update the register with values from Fetch stage
  IFID_reg.instruction := current_instr
  IFID_reg.pc          := current_pc



  // --- DECODE STAGE ---
  // Now you access them like this:
  decoder.io.input := IFID_reg.instruction


  // Registers
  val registers = Module(new Registers())
  registers.io.rs1 := decoder.io.rs1
  registers.io.rs2 := 0.U //decoder.io.rs2




  // --- ID/EX PIPELINE REGISTER --------------------------------------------------------
  val IDEX_reg = RegInit(0.U.asTypeOf(new IDEXBundle))
  dontTouch(IDEX_reg)
  IDEX_reg.pc := IFID_reg.pc
  IDEX_reg.instruction := IFID_reg.instruction
  IDEX_reg.rs1Data := registers.io.rs1Data
  IDEX_reg.rs2Data := registers.io.rs2Data
  IDEX_reg.opcode := decoder.io.opcode
  IDEX_reg.imm := decoder.io.imm



  // --- EXECUTE STAGE ---
  // here we execute with the ALU
  val ALU = Module(new ALU())
  ALU.io.a0 := IDEX_reg.rs1Data
  ALU.io.a1 := IDEX_reg.imm // only for now, add a mux here later
  ALU.io.sel := 0.U





  // --- EX/MEM PIPELINE REGISTER --------------------------------------------------------
  val EXMEM_reg = RegInit(0.U.asTypeOf(new EXMEMBundle))
  dontTouch(EXMEM_reg)
  EXMEM_reg.pc := IDEX_reg.pc
  EXMEM_reg.instruction := IDEX_reg.instruction
  EXMEM_reg.opcode := IDEX_reg.opcode
  EXMEM_reg.result := ALU.io.out
  EXMEM_reg.opcode := IDEX_reg.opcode



  // --- MEMORY STAGE ---
  // here we ask the memory for information




  // --- MEM/WB PIPELINE REGISTER --------------------------------------------------------
  val MEMWB_reg = RegInit(0.U.asTypeOf(new MEMWBBundle))
  dontTouch(MEMWB_reg)
  MEMWB_reg.pc := EXMEM_reg.pc
  MEMWB_reg.instruction := EXMEM_reg.instruction
  MEMWB_reg.opcode := EXMEM_reg.opcode
  MEMWB_reg.result := EXMEM_reg.result

  // --- WRITE BACK ---
  // if we should write back then do it

  registers.io.rd := MEMWB_reg.instruction(11,7)
  // Define your data sources (adjust names to match your registers/io)
  val aluResult = MEMWB_reg.result
  val memData   = MEMWB_reg.memoryVal // Data coming back from memory
  val pcPlus4   = MEMWB_reg.pc + 4.U // Return address for JAL/JALR

  registers.io.rdData := MuxCase(0.U, Seq(
    // ALU & Immediate operations use the ALU result
    (MEMWB_reg.opcode === "b0110111".U) -> aluResult, //MEMWB_reg.imm,       // LUI (usually just the immediate)
    (MEMWB_reg.opcode === "b0010111".U) -> aluResult,           // AUIPC
    (MEMWB_reg.opcode === "b0010011".U) -> aluResult,           // ALU Imm / Shift
    (MEMWB_reg.opcode === "b0110011".U) -> aluResult,           // ALU Reg

    // Jump instructions write the return address (PC + 4)
    (MEMWB_reg.opcode === "b1101111".U) -> pcPlus4,             // JAL
    (MEMWB_reg.opcode === "b1100111".U) -> pcPlus4,             // JALR

    // Load instructions use the data from memory
    (MEMWB_reg.opcode === "b0000011".U) -> memData              // Load
  ))


  registers.io.regWrite := MuxCase(false.B, Seq(
    (MEMWB_reg.opcode === "b0110111".U) -> true.B,  // LUI type
    (MEMWB_reg.opcode === "b0010111".U) -> true.B,  // AUIPC type
    (MEMWB_reg.opcode === "b1101111".U) -> true.B,  // JAL type
    (MEMWB_reg.opcode === "b1100111".U) -> true.B,  // JALR type
    (MEMWB_reg.opcode === "b0000011".U) -> true.B,  // MEMORY load type
    (MEMWB_reg.opcode === "b0010011".U) -> true.B,  // ALU register - immediate type
    (MEMWB_reg.opcode === "b0010011".U) -> true.B,  // SHIFT type
    (MEMWB_reg.opcode === "b0110011".U) -> true.B   // ALU register - register type
  ))




}




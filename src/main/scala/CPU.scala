import chisel3.{RegInit, dontTouch, _}
import chisel3.util._

// Import your custom package
import pipelineregisters._


// FETCH // DECODE // EXECUTE // MEMORY // WRITEBACK

class CPU(ProgPath: String) extends Module {
  val io = IO(new Bundle {
    val regs = Output(Vec(32,UInt(32.W)))
  })
  val decoder = Module(new Decoder())


  val PC = RegInit(0.U(32.W))
  val PCMuxSel = RegInit(0.U(2.W))

  PC := MuxCase(0.U, Seq(
    (PCMuxSel === 0.U) -> (PC + 4.U), // this is the normal operation
    (PCMuxSel === 1.U) -> PC,     // dont increment
    (PCMuxSel === 2.U) -> (decoder.io.imm << 1) //
  ))


  val ProgMem = Module(new Memory(ProgPath))



  // --- FETCH STAGE ---
  ProgMem.io.instAddr := PC
  val current_instr = ProgMem.io.inst
  val current_pc    = PC



  // --- IF/ID PIPELINE REGISTER --------------------------------------------------------
  val IFID = Module(new IFID())
  // Update the register with values from Fetch stage
  IFID.io.in.instruction := current_instr
  IFID.io.in.pc          := current_pc
  IFID.io.en := 1.U
  IFID.io.clear := 0.U



  // --- DECODE STAGE ---
  // Now you access them like this:
  decoder.io.input := IFID.io.out.instruction


  // Registers
  val registers = Module(new Registers())
  registers.io.rs1 := decoder.io.rs1
  registers.io.rs2 := decoder.io.rs2
  io.regs := registers.io.regs






  // --- ID/EX PIPELINE REGISTER --------------------------------------------------------
  val IDEX_reg = RegInit(0.U.asTypeOf(new IDEXBundle))
  val IDEX = Module(new IDEX())
  IDEX.io.en := 1.U
  IDEX.io.clear := 0.U


  IDEX.io.in.pc := IFID.io.out.pc
  IDEX.io.in.instruction := IFID.io.out.instruction
  IDEX.io.in.rs1Data := registers.io.rs1Data
  IDEX.io.in.rs2Data := registers.io.rs2Data
  IDEX.io.in.opcode := decoder.io.opcode
  IDEX.io.in.imm := decoder.io.imm

  val control = Module(new Control())
  dontTouch(control.io)
  control.io.opcode := decoder.io.opcode
  control.io.func3 := decoder.io.func3
  control.io.func7 := decoder.io.func7

  IDEX.io.in.ALUsrc := control.io.ALUsrc
  IDEX.io.in.ALUctrl := control.io.ALUctrl


  // --- EXECUTE STAGE ---
  // here we execute with the ALU
  val ALU = Module(new ALU())
  ALU.io.a0 := IDEX.io.out.rs1Data
  ALU.io.a1 := MuxCase(0.U, Seq(
    (IDEX.io.out.ALUsrc === 0.U) -> IDEX.io.out.rs2Data,
    (IDEX.io.out.ALUsrc === 1.U) -> IDEX.io.out.imm
  ))
  //ALU.io.a1 := IDEX_reg.imm // only for now, add a mux here later
  //ALU.io.sel := 0.U
  ALU.io.sel := IDEX.io.out.ALUctrl





  // --- EX/MEM PIPELINE REGISTER --------------------------------------------------------
  val EXMEM = Module(new EXMEM())
  EXMEM.io.en := 1.U
  EXMEM.io.clear := 0.U

  EXMEM.io.in.pc := IDEX.io.out.pc
  EXMEM.io.in.instruction := IDEX.io.out.instruction
  EXMEM.io.in.opcode := IDEX.io.out.opcode
  EXMEM.io.in.result := ALU.io.out
  EXMEM.io.in.func3 := decoder.io.func3
  EXMEM.io.in.func7 := decoder.io.func7
  EXMEM.io.in.rs2Data := IDEX.io.out.rs2Data


  // --- MEMORY STAGE ---
  // here we ask the memory for information

  ProgMem.io.rs2Data := EXMEM.io.out.rs2Data
  ProgMem.io.dataAddr := EXMEM.io.out.result
  ProgMem.io.func3 := EXMEM.io.out.func3
  ProgMem.io.opcode := EXMEM.io.out.opcode



  // --- MEM/WB PIPELINE REGISTER --------------------------------------------------------
  val MEMWB = Module(new MEMWB())
  MEMWB.io.en := 1.U
  MEMWB.io.clear := 0.U
  MEMWB.io.in.pc := EXMEM.io.out.pc
  MEMWB.io.in.instruction := EXMEM.io.out.instruction
  MEMWB.io.in.opcode := EXMEM.io.out.opcode
  MEMWB.io.in.result := EXMEM.io.out.result
  MEMWB.io.in.memoryVal := ProgMem.io.readData // placeholder the memory controller hasnt been implemented yet
  MEMWB.io.in.func3 := EXMEM.io.out.func3
  MEMWB.io.in.func7 := EXMEM.io.out.func7


  // --- WRITE BACK ---
  // if we should write back then do it

  registers.io.rd := MEMWB.io.out.instruction(11,7)

  registers.io.rdData := MuxCase(0.U, Seq(
    // ALU & Immediate operations use the ALU result
    (MEMWB.io.out.opcode === "b0110111".U) -> MEMWB.io.out.result, //MEMWB_reg.imm,       // LUI (usually just the immediate)
    (MEMWB.io.out.opcode === "b0010111".U) -> MEMWB.io.out.result,           // AUIPC
    (MEMWB.io.out.opcode === "b0010011".U) -> MEMWB.io.out.result,           // ALU Imm / Shift
    (MEMWB.io.out.opcode === "b0110011".U) -> MEMWB.io.out.result,           // ALU Reg

    // Jump instructions write the return address (PC + 4)
    (MEMWB.io.out.opcode === "b1101111".U) -> (MEMWB.io.out.pc + 4.U),             // JAL
    (MEMWB.io.out.opcode === "b1100111".U) -> (MEMWB.io.out.pc + 4.U),             // JALR

    // Load instructions use the data from memory
    (MEMWB.io.out.opcode === "b0000011".U) -> MEMWB.io.out.memoryVal              // Load
  ))


  registers.io.regWrite := MuxCase(false.B, Seq(
    (MEMWB.io.out.opcode === "b0110111".U) -> true.B,  // LUI type
    (MEMWB.io.out.opcode === "b0010111".U) -> true.B,  // AUIPC type
    (MEMWB.io.out.opcode === "b1101111".U) -> true.B,  // JAL type
    (MEMWB.io.out.opcode === "b1100111".U) -> true.B,  // JALR type
    (MEMWB.io.out.opcode === "b0000011".U) -> true.B,  // MEMORY load type
    (MEMWB.io.out.opcode === "b0010011".U) -> true.B,  // ALU register - immediate type
    (MEMWB.io.out.opcode === "b0010011".U) -> true.B,  // SHIFT type
    (MEMWB.io.out.opcode === "b0110011".U) -> true.B   // ALU register - register type
  ))
  //



}




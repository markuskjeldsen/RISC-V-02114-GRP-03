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
  val HazardDetection = Module(new HazardDetection())



  PC := MuxCase(0.U, Seq(
    (HazardDetection.io.out.PCen === 1.U) -> (PC + 4.U), // this is the normal operation
    (HazardDetection.io.out.PCen === 0.U) -> PC,     // dont increment
  ))

/*  PC := MuxCase(0.U, Seq(
    (PCMuxSel === 0.U) -> (PC + 4.U), // this is the normal operation
    (PCMuxSel === 1.U) -> PC,     // dont increment
    (PCMuxSel === 2.U) -> (decoder.io.imm << 1) //
  ))
*/

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
  IFID.io.en := HazardDetection.io.out.IFIDen
  IFID.io.clear := HazardDetection.io.out.IFIDclear



  // --- DECODE STAGE ---
  // Now you access them like this:
  decoder.io.input := IFID.io.out.instruction


  // Registers
  val registers = Module(new Registers())
  registers.io.rs1 := decoder.io.rs1
  registers.io.rs2 := decoder.io.rs2
  io.regs := registers.io.regs
  // Instantiate pipeline registers
  val IDEX = Module(new IDEX())
  val EXMEM = Module(new EXMEM())
  val MEMWB = Module(new MEMWB())
  val control = Module(new Control())
  // --- ID/EX PIPELINE REGISTER --------------------------------------------------------
  IDEX.io.en := HazardDetection.io.out.IDEXen
  IDEX.io.clear := HazardDetection.io.out.IDEXclear
  IDEX.io.in.rs1 := decoder.io.rs1
  IDEX.io.in.rs2 := decoder.io.rs2
  IDEX.io.in.pc := IFID.io.out.pc
  IDEX.io.in.instruction := IFID.io.out.instruction
  IDEX.io.in.rs1Data := registers.io.rs1Data
  IDEX.io.in.rs2Data := registers.io.rs2Data
  IDEX.io.in.opcode := decoder.io.opcode
  IDEX.io.in.imm := decoder.io.imm
  IDEX.io.in.regWrite := control.io.regWrite

  HazardDetection.io.in.IFIDinstruction := IFID.io.out.instruction
  HazardDetection.io.in.IDEXinstruction := IDEX.io.out.instruction
  HazardDetection.io.in.EXMEMinstruction := EXMEM.io.out.instruction
  HazardDetection.io.in.MEMWBinstruction := MEMWB.io.out.instruction






  dontTouch(control.io)
  control.io.opcode := decoder.io.opcode
  control.io.func3 := decoder.io.func3
  control.io.func7 := decoder.io.func7

// ALU signals
  IDEX.io.in.ALUsrc := control.io.ALUsrc
  IDEX.io.in.ALUctrl := control.io.ALUctrl
  IDEX.io.in.ControlBool := (decoder.io.opcode === "b1100011".U)
  IDEX.io.in.BranchCtrl := control.io.BranchCtrl

  IDEX.io.in.ra := IFID.io.out.pc
  //Jump signals
  when(IDEX.io.out.opcode === "b1101111".U) {

    PC := MuxCase(0.U, Seq(

      // JAL target = PC + imm
      (decoder.io.opcode === "b1101111".U) -> (IFID.io.out.pc + decoder.io.imm).asUInt,

      // JALR target = rs1 + imm
      (decoder.io.opcode === "b1100111".U) -> (decoder.io.rs1 + decoder.io.imm).asUInt

    ))
  }

  // Forwarding begins
  // 00 = no forwarding
  // 10 = from EX/MEM
  // 01 = from MEM/WB
  val forwardA = Wire(UInt(2.W))
  val forwardB = Wire(UInt(2.W))
  forwardA := "b00".U
  forwardB := "b00".U

  when(EXMEM.io.out.regWrite &&
    EXMEM.io.out.rd =/= 0.U &&
    EXMEM.io.out.rd === IDEX.io.out.rs1) {
    forwardA := "b10".U
  }

  when(EXMEM.io.out.regWrite &&
    EXMEM.io.out.rd =/= 0.U &&
    EXMEM.io.out.rd === IDEX.io.out.rs2) {
    forwardB := "b10".U
  }

  when(MEMWB.io.out.regWrite &&
    MEMWB.io.out.rd =/= 0.U &&
    !(EXMEM.io.out.regWrite && EXMEM.io.out.rd === IDEX.io.out.rs1) &&
    MEMWB.io.out.rd === IDEX.io.out.rs1) {
    forwardA := "b01".U
  }

  when(MEMWB.io.out.regWrite &&
    MEMWB.io.out.rd =/= 0.U &&
    !(EXMEM.io.out.regWrite && EXMEM.io.out.rd === IDEX.io.out.rs2) &&
    MEMWB.io.out.rd === IDEX.io.out.rs2) {
    forwardB := "b01".U
  }


  // --- EXECUTE STAGE ---
  // here we execute with the ALU
  // Forwarded register operands
  val rs1Forwarded = MuxCase(IDEX.io.out.rs1Data, Seq(
    (forwardA === "b10".U) -> EXMEM.io.out.result,
    (forwardA === "b01".U) -> MEMWB.io.out.result//registers.io.rdData
  ))

  val rs2Forwarded = MuxCase(IDEX.io.out.rs2Data, Seq(
    (forwardB === "b10".U) -> EXMEM.io.out.result,
    (forwardB === "b01".U) -> MEMWB.io.out.result//registers.io.rdData
  ))

  val ALU = Module(new ALU())
  val ALUa = Mux(
    IDEX.io.out.opcode === "b0010111".U, // AUIPC
    IDEX.io.out.pc,                     // use PC
    rs1Forwarded                        // forwarded rs1
  )
  ALU.io.a0 := ALUa
  ALU.io.a1 := MuxCase(0.U, Seq(
    (IDEX.io.out.ALUsrc === 0.U) -> rs2Forwarded,
    (IDEX.io.out.ALUsrc === 1.U) -> IDEX.io.out.imm
  ))
  //ALU.io.a1 := IDEX_reg.imm // only for now, add a mux here later
  //ALU.io.sel := 0.U
  ALU.io.sel := IDEX.io.out.ALUctrl

  val branches = Module(new Branches())
  branches.io.a0 := rs1Forwarded
  branches.io.a1 := rs2Forwarded
  branches.io.sel := IDEX.io.out.BranchCtrl

  val branchTaken = IDEX.io.out.ControlBool && branches.io.out
  val branchTarget = IDEX.io.out.pc + IDEX.io.out.imm


  // JUMP implementaion
 // when(IDEX.io.out.opcode === "b1101111".U){PC:= IDEX.io.out.targetAddress}


  // --- EX/MEM PIPELINE REGISTER --------------------------------------------------------
  EXMEM.io.en := 1.U
  EXMEM.io.clear := 0.U
  when(branchTaken){
    EXMEM.io.in.pc := branchTarget
  }.otherwise{
    EXMEM.io.in.pc := IDEX.io.out.pc
  }
  EXMEM.io.in.instruction := IDEX.io.out.instruction
  EXMEM.io.in.opcode := IDEX.io.out.opcode
  EXMEM.io.in.result := ALU.io.out
  EXMEM.io.in.func3 := decoder.io.func3
  EXMEM.io.in.func7 := decoder.io.func7
  EXMEM.io.in.rs2Data := rs2Forwarded
  EXMEM.io.in.rd := IDEX.io.out.instruction(11,7)
  EXMEM.io.in.regWrite := IDEX.io.out.regWrite
  EXMEM.io.in.ra := IDEX.io.out.ra

  // --- MEMORY STAGE ---
  // here we ask the memory for information

  ProgMem.io.rs2Data := EXMEM.io.out.rs2Data
  ProgMem.io.dataAddr := EXMEM.io.out.result
  ProgMem.io.func3 := EXMEM.io.out.func3
  ProgMem.io.opcode := EXMEM.io.out.opcode



  // --- MEM/WB PIPELINE REGISTER --------------------------------------------------------
  MEMWB.io.en := 1.U
  MEMWB.io.clear := 0.U
  MEMWB.io.in.pc := EXMEM.io.out.pc
  MEMWB.io.in.instruction := EXMEM.io.out.instruction
  MEMWB.io.in.opcode := EXMEM.io.out.opcode
  MEMWB.io.in.result := EXMEM.io.out.result
  MEMWB.io.in.memoryVal := ProgMem.io.readData // placeholder the memory controller hasnt been implemented yet
  MEMWB.io.in.func3 := EXMEM.io.out.func3
  MEMWB.io.in.func7 := EXMEM.io.out.func7
  MEMWB.io.in.rd := EXMEM.io.out.rd
  MEMWB.io.in.regWrite := EXMEM.io.out.regWrite



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
    (MEMWB.io.out.opcode === "b1101111".U) -> (MEMWB.io.out.pc),             // JAL
    (MEMWB.io.out.opcode === "b1100111".U) -> (MEMWB.io.out.pc + 4.U),             // JALR

    // Load instructions use the data from memory
    (MEMWB.io.out.opcode === "b0000011".U) -> ProgMem.io.readData              // Load must be the actual data because data is one cycle late
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




}




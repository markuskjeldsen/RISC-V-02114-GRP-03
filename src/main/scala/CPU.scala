import chisel3.{RegInit, _}
import chisel3.util._
import pipelineregisters._


object CPU extends App {
  emitVerilog(
    new CPU("src/test/scala/programs/Blink.hex",true),
    Array("--target-dir", "generated")
  )
}

class CPU(ProgPath: String, debug : Boolean ) extends Module {
  val io = IO(new Bundle {
    val regs = if (debug) Some(Output(Vec(32, UInt(32.W)))) else None
    val led = Output(Bool())
  })

  val decoder = Module(new Decoder())
  val control = Module(new Control())
  val HazardDetection = Module(new HazardDetection())

  val IDEX  = Module(new IDEX())
  val EXMEM = Module(new EXMEM())
  val MEMWB = Module(new MEMWB())

  val ProgMem = Module(new Memory(ProgPath))

  // ---------------------------
  // PC + FETCH (SyncReadMem => 1-cycle instruction latency)
  // ---------------------------
  val PC = RegInit(0.U(32.W))

  ProgMem.io.instAddr  := PC
  ProgMem.io.instClear := false.B // flush is handled in IF/ID, not inside Memory

  // PC that corresponds to the instruction that arrives next cycle
  val fetchPCReg = RegNext(PC)

  // ---------------------------
  // IF/ID pipeline registers
  // ---------------------------
  val IFID_inst = RegInit("h00000013".U(32.W)) // NOP
  val IFID_pc   = RegInit(0.U(32.W))

  // Hazard unit inputs
  HazardDetection.io.in.IFIDinstruction := IFID_inst
  HazardDetection.io.in.IDEXinstruction := IDEX.io.out.instruction

  // ---------------------------
  // DECODE (use IF/ID regs)
  // ---------------------------
  decoder.io.input := IFID_inst

  val registers = Module(new Registers())
  registers.io.rs1 := decoder.io.rs1
  registers.io.rs2 := decoder.io.rs2





  // "If regs exists, connect it, otherwise, do nothing."
  // Option B: The "Explicit" way
  if (debug) {
    io.regs.get := registers.io.regs
  }


  io.led := registers.io.regs(7)(0)

  control.io.opcode := decoder.io.opcode
  control.io.func3  := decoder.io.func3
  control.io.func7  := decoder.io.func7

  // ---------------------------
  // ID/EX pipeline register
  // ---------------------------
  IDEX.io.en := HazardDetection.io.out.IDEXen && HazardDetection.io.out.PCen

  val idexClearFromHazard = HazardDetection.io.out.IDEXclear
  IDEX.io.clear := idexClearFromHazard

  IDEX.io.in.rs1         := decoder.io.rs1
  IDEX.io.in.rs2         := decoder.io.rs2
  IDEX.io.in.pc          := IFID_pc
  IDEX.io.in.instruction := IFID_inst
  IDEX.io.in.rs1Data     := registers.io.rs1Data
  IDEX.io.in.rs2Data     := registers.io.rs2Data
  IDEX.io.in.opcode      := decoder.io.opcode
  IDEX.io.in.imm         := decoder.io.imm

  IDEX.io.in.regWrite   := control.io.regWrite
  IDEX.io.in.loadedData := control.io.loadedData

  IDEX.io.in.ALUsrc      := control.io.ALUsrc
  IDEX.io.in.ALUctrl     := control.io.ALUctrl
  IDEX.io.in.ControlBool := (decoder.io.opcode === "b1100011".U) // isBranch
  IDEX.io.in.BranchCtrl  := control.io.BranchCtrl

  // return address for JAL/JALR
  IDEX.io.in.ra := IFID_pc + 4.U

  // ---------------------------
  // Forwarding
  // 00 = none
  // 10 = EX/MEM
  // 01 = MEM/WB result
  // 11 = MEM/WB load data (memoryVal)
  // ---------------------------
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

  when(MEMWB.io.out.regWrite && MEMWB.io.out.loadedData &&
    MEMWB.io.out.rd =/= 0.U &&
    !(EXMEM.io.out.regWrite && EXMEM.io.out.rd === IDEX.io.out.rs1) &&
    MEMWB.io.out.rd === IDEX.io.out.rs1) {
    forwardA := "b11".U
  }

  when(MEMWB.io.out.regWrite && MEMWB.io.out.loadedData &&
    MEMWB.io.out.rd =/= 0.U &&
    !(EXMEM.io.out.regWrite && EXMEM.io.out.rd === IDEX.io.out.rs2) &&
    MEMWB.io.out.rd === IDEX.io.out.rs2) {
    forwardB := "b11".U
  }

  val rs1Forwarded = MuxCase(IDEX.io.out.rs1Data, Seq(
    (forwardA === "b10".U) -> EXMEM.io.out.result,
    (forwardA === "b01".U) -> MEMWB.io.out.result,
    (forwardA === "b11".U) -> MEMWB.io.out.memoryVal
  ))

  val rs2Forwarded = MuxCase(IDEX.io.out.rs2Data, Seq(
    (forwardB === "b10".U) -> EXMEM.io.out.result,
    (forwardB === "b01".U) -> MEMWB.io.out.result,
    (forwardB === "b11".U) -> MEMWB.io.out.memoryVal
  ))

  // ---------------------------
  // EXECUTE
  // ---------------------------
  val ALU = Module(new ALU())

  // AUIPC uses the PC of the instruction (from IDEX)
  val ALUa = Mux(
    IDEX.io.out.opcode === "b0010111".U, // AUIPC
    IDEX.io.out.pc,
    rs1Forwarded
  )

  ALU.io.a0 := ALUa
  ALU.io.a1 := Mux(IDEX.io.out.ALUsrc === 1.U, IDEX.io.out.imm, rs2Forwarded)
  ALU.io.sel := IDEX.io.out.ALUctrl

  val branches = Module(new Branches())
  branches.io.a0 := rs1Forwarded
  branches.io.a1 := rs2Forwarded
  branches.io.sel := IDEX.io.out.BranchCtrl

  val branchTaken  = Wire(Bool())
  val branchTarget = Wire(UInt(32.W))
  branchTaken  := IDEX.io.out.ControlBool && branches.io.out
  branchTarget := IDEX.io.out.pc + IDEX.io.out.imm

  HazardDetection.io.in.pcFromTakenBranch := branchTaken

  // ---------------------------
  // Jump resolution in EX
  // ---------------------------
  val exIsJal  = (IDEX.io.out.opcode === "b1101111".U)
  val exIsJalr = (IDEX.io.out.opcode === "b1100111".U)
  val exJumpTaken = exIsJal || exIsJalr

  val exJumpTarget = Mux(
    exIsJal,
    (IDEX.io.out.pc + IDEX.io.out.imm).asUInt,
    ((rs1Forwarded + IDEX.io.out.imm) & (~1.U(32.W))).asUInt
  )

  // ---------------------------
  // kill fetched instruction after redirect (SyncReadMem imem)
  // ---------------------------
  val redirectTaken   = branchTaken || exJumpTaken
  val killFetchedInst = RegNext(redirectTaken, init = false.B)

  // ---------------------------
  // IF/ID update (stall + flush + killFetchedInst bubble)
  // ---------------------------
  when(HazardDetection.io.out.IFIDen) {
    when(killFetchedInst || HazardDetection.io.out.IFIDclear) {
      IFID_inst := "h00000013".U
      IFID_pc   := fetchPCReg
    }.otherwise {
      IFID_inst := ProgMem.io.inst
      IFID_pc   := fetchPCReg
    }
  }

  // ---------------------------
  // Single PC update
  // ---------------------------
  val pcPlus4 = PC + 4.U
  val pcHold  = !HazardDetection.io.out.PCen

  val nextPC = Mux(
    pcHold,
    PC,
    Mux(
      exJumpTaken,
      exJumpTarget,
      Mux(
        branchTaken,
        branchTarget,
        pcPlus4
      )
    )
  )

  PC := nextPC

  // ---------------------------
  // EX/MEM pipeline register
  // ---------------------------
  EXMEM.io.en    := true.B
  EXMEM.io.clear := false.B

  EXMEM.io.in.pc          := IDEX.io.out.pc
  EXMEM.io.in.instruction := IDEX.io.out.instruction
  EXMEM.io.in.opcode      := IDEX.io.out.opcode
  EXMEM.io.in.result      := ALU.io.out

  EXMEM.io.in.func3 := IDEX.io.out.instruction(14, 12)
  EXMEM.io.in.func7 := IDEX.io.out.instruction(31, 25)

  EXMEM.io.in.rs2Data     := rs2Forwarded
  EXMEM.io.in.rd          := IDEX.io.out.instruction(11, 7)
  EXMEM.io.in.regWrite    := IDEX.io.out.regWrite
  EXMEM.io.in.ra          := IDEX.io.out.ra
  EXMEM.io.in.loadedData  := IDEX.io.out.loadedData

  // ---------------------------
  // MEMORY stage
  // ---------------------------
  ProgMem.io.rs2Data  := EXMEM.io.out.rs2Data
  ProgMem.io.dataAddr := EXMEM.io.out.result
  ProgMem.io.func3    := EXMEM.io.out.func3
  ProgMem.io.opcode   := EXMEM.io.out.opcode

  // ---------------------------
  // MEM/WB pipeline register
  // ---------------------------
  MEMWB.io.en    := true.B
  MEMWB.io.clear := false.B

  MEMWB.io.in.pc          := EXMEM.io.out.pc
  MEMWB.io.in.instruction := EXMEM.io.out.instruction
  MEMWB.io.in.opcode      := EXMEM.io.out.opcode
  MEMWB.io.in.result      := EXMEM.io.out.result
  MEMWB.io.in.memoryVal   := ProgMem.io.readData
  MEMWB.io.in.func3       := EXMEM.io.out.func3
  MEMWB.io.in.func7       := EXMEM.io.out.func7
  MEMWB.io.in.rd          := EXMEM.io.out.rd
  MEMWB.io.in.regWrite    := EXMEM.io.out.regWrite
  MEMWB.io.in.ra          := EXMEM.io.out.ra
  MEMWB.io.in.loadedData  := EXMEM.io.out.loadedData

  // ---------------------------
  // WRITEBACK
  // ---------------------------
  registers.io.rd := MEMWB.io.out.rd

  val wbData = MuxCase(MEMWB.io.out.result, Seq(
    (MEMWB.io.out.opcode === "b1101111".U) -> MEMWB.io.out.ra,
    (MEMWB.io.out.opcode === "b1100111".U) -> MEMWB.io.out.ra,
    (MEMWB.io.out.opcode === "b0000011".U) -> MEMWB.io.out.memoryVal
  ))

  registers.io.rdData   := wbData
  registers.io.regWrite := MEMWB.io.out.regWrite
}

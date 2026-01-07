import chisel3._
import chisel3.util._

import chisel3._
import chisel3.util._
// Import your custom package
import pipelineregisters._


// FETCH // DECODE // EXECUTE // MEMORY // WRITEBACK

class CPU() extends Module {
  val io = IO(new Bundle {
    val PRGCNT = Input(UInt(32.W))
  })

  val ProgMem = Module(new ProgramMemory())

  // --- FETCH STAGE ---
  ProgMem.io.ProgramCounter := io.PRGCNT
  val current_instr = ProgMem.io.instruction
  val current_pc    = io.PRGCNT

  // --- IF/ID PIPELINE REGISTER ---
  val IFID_reg = RegInit(0.U.asTypeOf(new IFIDBundle))

  // Update the register with values from Fetch stage
  IFID_reg.instruction := current_instr
  IFID_reg.pc          := current_pc



  // --- DECODE STAGE ---
  // Now you access them like this:
  val opcode = IFID_reg.instruction(6, 0)
  val pc_in_decode = IFID_reg.pc




  // ID/EX PIPELINE REGISTER
  val IDEX_reg = RegInit(0.U.asTypeOf(new IDEXBundle))
  IDEX_reg.pc := IFID_reg.pc
  IDEX_reg.instruction := IFID_reg.instruction


  // --- EXECUTE STAGE ---
  // here we execute with the ALU
  val ALU = Module(new ALU())





  // EX/MEM


  // --- MEMORY STAGE ---
  // here we ask the memory for information




  // MEM/WB
  // PIPELINE STAGE


  // --- WRITE BACK ---


}




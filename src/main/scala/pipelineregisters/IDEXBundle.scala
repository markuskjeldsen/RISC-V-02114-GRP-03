package pipelineregisters // This must match the folder name

import chisel3._

class IDEXBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
  val opcode      = UInt(7.W)
  val rs1Data     = UInt(32.W)
  val rs2Data     = UInt(32.W)
  val ALUsrc      = UInt(1.W)
  val imm         = UInt(32.W)
  val ALUctrl     = UInt(4.W)
}

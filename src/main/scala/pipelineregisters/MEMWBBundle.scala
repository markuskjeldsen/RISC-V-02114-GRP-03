package pipelineregisters // This must match the folder name

import chisel3._

class MEMWBBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
  val opcode      = UInt(7.W)
  val result      = SInt(32.W)
}

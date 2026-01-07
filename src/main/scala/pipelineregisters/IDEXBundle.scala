package pipelineregisters // This must match the folder name

import chisel3._

class IDEXBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
  val a0          = SInt(32.W)
  val a1          = SInt(32.W)
  val sel         = UInt(2.W)
}

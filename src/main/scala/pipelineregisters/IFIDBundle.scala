package pipelineregisters // This must match the folder name

import chisel3._

class IFIDBundle extends Bundle {
  val instruction = UInt(32.W)
  val pc          = UInt(32.W)
}

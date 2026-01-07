import chisel3._
import chisel3.util._

class Memory_load_instructions {
  def LB(addr: UInt, memWord: UInt): UInt = {
    val byteOffset = addr(1, 0)          // which byte inside the word
    val shifted    = memWord >> (byteOffset << 3)
    val byte       = shifted(7, 0)

    // sign-extend 8-bit value to 32 bits
    Cat(Fill(24, byte(7)), byte)
  }

  def LH(addr: UInt, memWord: UInt): UInt = {
    val halfOffset = addr(1)              // 0 = lower half, 1 = upper half
    val shifted    = memWord >> (halfOffset << 4)
    val half       = shifted(15, 0)

    // sign-extend 16-bit value to 32 bits
    Cat(Fill(16, half(15)), half)
  }

  def loadWord(addr: UInt, memWord: UInt): UInt = {
    // For LW, the memory already returns the correct 32-bit word
    memWord
  }

  def loadByteUnsigned(addr: UInt, memWord: UInt): UInt = {
    val byteOffset = addr(1, 0)
    val shifted    = memWord >> (byteOffset << 3)
    val byte       = shifted(7, 0)

    // zero-extend 8-bit value to 32 bits
    Cat(0.U(24.W), byte)
  }

  def loadHalfUnsigned(addr: UInt, memWord: UInt): UInt = {
    val halfOffset = addr(1)
    val shifted    = memWord >> (halfOffset << 4)
    val half       = shifted(15, 0)

    // zero-extend 16-bit value to 32 bits
    Cat(0.U(16.W), half)
  }

}

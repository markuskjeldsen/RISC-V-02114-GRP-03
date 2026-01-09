import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUIRIITest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUIRIITest" should "pass" in {
    test(new CPU("src/test/scala/programs/IRII.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(72)
      dut.io.regs(1).expect(5)
      dut.io.regs(2).expect(BigInt("FFFFFFFD", 16))
      dut.io.regs(3).expect(0x0000000f)
      dut.io.regs(4).expect(0)
      dut.io.regs(5).expect(1)
      dut.io.regs(6).expect(0)
      dut.io.regs(7).expect(1)
      dut.io.regs(8).expect(0)
      dut.io.regs(9).expect(3)
      dut.io.regs(10).expect(BigInt("FFFFFFFA", 16))
      dut.io.regs(11).expect(7)
      dut.io.regs(12).expect(BigInt("FFFFFFFF", 16))
      dut.io.regs(13).expect(1)
      dut.io.regs(14).expect(5)
      //addi  x1,  x0, 5
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x2,  x0, -3
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x3,  x1, 10
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x4,  x1, -5
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //slti  x5,  x1, 10
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //slti  x6,  x1, 3
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //sltiu x7,  x1, 10
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //sltiu x8,  x2, 10
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //xori  x9,  x1, 6
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //xori  x10, x1, -1
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //ori   x11, x1, 2
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //ori   x12, x1, -1
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //andi  x13, x1, 3
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //addi  x0,  x0, 0
      //andi  x14, x1, -1
    }
  }
}

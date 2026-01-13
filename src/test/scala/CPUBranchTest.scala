import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUBranchTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBranchTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Branch.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(60)

      dut.io.regs(15).expect(BigInt("00000010",16))
      dut.io.regs(16).expect(BigInt("00000011",16))
      dut.io.regs(17).expect(BigInt("00000012",16))
      dut.io.regs(18).expect(BigInt("00000013",16))
      dut.io.regs(19).expect(BigInt("FFFFFFFF",16))

    }
  }
}

import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUBranchTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPUBranchTest" should "pass" in {
    test(new CPU("src/test/scala/programs/Branch.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(60)

      dut.io.regs(13).expect(BigInt("FFFFFFFF",16))

    }
  }
}

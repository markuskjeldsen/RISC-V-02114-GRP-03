import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPU" should "pass" in {
    test(new CPU("src/test/scala/addi.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.clock.step(1)
      dut.clock.step(1)
      dut.clock.step(1)
      dut.clock.step(1)
      dut.clock.step(1)
      dut.clock.step(1)
      dut.clock.step(1)
      dut.clock.step(1)

      dut.clock.step(10)
    }
  }
}

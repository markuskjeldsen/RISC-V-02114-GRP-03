import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUTest extends AnyFlatSpec with ChiselScalatestTester {
  "CPU" should "pass" in {
    test(new CPU()).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
      // Increase timeout to inf cycles
      dut.clock.setTimeout(0)
      dut.io.PRGCNT.poke(0)
      dut.clock.step(1)
      dut.io.PRGCNT.poke(4)
      dut.clock.step(1)
      dut.io.PRGCNT.poke(8)
      dut.clock.step(1)
      dut.io.PRGCNT.poke(12)
      dut.clock.step(1)
      dut.io.PRGCNT.poke(16)
      dut.clock.step(1)

      dut.clock.step(10)
    }
  }
}

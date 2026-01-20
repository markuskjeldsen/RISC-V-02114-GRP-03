import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

class CPUHazardTest extends AnyFlatSpec with ChiselScalatestTester {
    "CPUHazard" should "pass" in {
        test(new CPU("src/test/scala/programs/HazardLoadUse.hex")).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>
            // Increase timeout to inf cycles
            dut.clock.setTimeout(0)
            dut.clock.step(72)
            // addi x10, x0 , 30
            // addi x0, x0, 0
            // addi x0, x0, 0
            // addi x0, x0, 0
            // sw x10, -4(sp)
            // addi x0, x0, 0

            // addi x0, x0, 0
            // addi x0, x0, 0
            // lw x10, -4(sp) it should stall here
            // add x11, x10, x0
            // addi x0, x0, 0
            // addi x0, x0, 0
            // addi x0, x0, 0
        }
    }
}

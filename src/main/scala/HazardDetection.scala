import chisel3._
import chisel3.util._

class HazardDetection extends Module {
  val io = IO(new Bundle {
    val in = Input(new Bundle {
      // Raw Instructions
      val IFIDinstruction  = UInt(32.W)
      val IDEXinstruction  = UInt(32.W)

      // OPTIONAL: Usually you simply take the boolean result from the Branch Unit
      // false = PC+4, true = Branch Target
      val pcFromTakenBranch = Bool()
    })

    val out = Output(new Bundle {
      val IFIDen    = Bool()
      val IFIDclear = Bool() // Flush bit

      val IDEXen    = Bool()
      val IDEXclear = Bool() // Flush bit (insert bubble)

      val PCen      = Bool()
    })
  })

  // 1. Extract Fields from IF/ID (Current Instruction in Decode)
  val rs1_addr_IFID = io.in.IFIDinstruction(19, 15)
  val rs2_addr_IFID = io.in.IFIDinstruction(24, 20)

  // 2. Extract Fields from ID/EX (Previous Instruction in Execute)
  val rd_addr_IDEX  = io.in.IDEXinstruction(11, 7)
  val opcode_IDEX   = io.in.IDEXinstruction(6, 0)

  // RISC-V Opcode Constants
  val LOAD_OP   = "b0000011".U

  // 3. Determine instruction usage
  // We need to know if the instruction in ID/EX is actually a Load
  val idex_is_load = opcode_IDEX === LOAD_OP

  // We need to know if the instruction in IF/ID uses rs1 or rs2.
  // We can look at the opcode of the instruction currently in Decode.
  val opcode_IFID = io.in.IFIDinstruction(6, 0)

  // R-Type, I-Type, S-Type, B-Type usually use RS1. U-Type (LUI/AUIPC) and J-Type (JAL) do not.
  // Simplified check: LUI(0110111), AUIPC(0010111), JAL(1101111) don't use RS1.
  val uses_rs1 = opcode_IFID =/= "b0110111".U && opcode_IFID =/= "b0010111".U && opcode_IFID =/= "b1101111".U

  // R-Type, S-Type, B-Type use RS2. I-Type, U-Type, J-Type do not.
  // Simply: Only R-Type (0110011), Store (0100011), Branch (1100011) use RS2.
  val uses_rs2 = opcode_IFID === "b0110011".U || opcode_IFID === "b0100011".U || opcode_IFID === "b1100011".U

  // ---------------------------------------------
  // Default State: Everything Defines Normal Flow
  // ---------------------------------------------
  io.out.PCen      := true.B
  io.out.IFIDen    := true.B
  io.out.IFIDclear := false.B
  io.out.IDEXen    := true.B
  io.out.IDEXclear := false.B

  // ---------------------------------------------
  // HAZARD 1: Load-Use Hazard
  // ---------------------------------------------
  // Scenario:
  // ID/EX has a Load Instruction.
  // IF/ID depends on that Load's result (rd).
  // Logic: Stall 1 Cycle.

  val stall_condition = idex_is_load && (rd_addr_IDEX =/= 0.U) &&
      ((uses_rs1 && rs1_addr_IFID === rd_addr_IDEX) ||
          (uses_rs2 && rs2_addr_IFID === rd_addr_IDEX))

  when(stall_condition) {
    // 1. Freeze PC (Prevent fetching new instruction)
    io.out.PCen   := false.B

    // 2. Freeze IF/ID (Keep the dependent instruction in Decode to try again next cycle)
    io.out.IFIDen := false.B

    // 3. Flush ID/EX (Send NOPs to Execute for one cycle, creating a bubble)
    // IMPORTANT: We do not disable IDEXen. We want it to tick, but capture 0 (bubble).
    io.out.IDEXclear := true.B
  }

  // ---------------------------------------------
  // HAZARD 2: Control Hazard (Branch Taken)
  // ---------------------------------------------
  // If the logic elsewhere decides a branch is taken, the instruction
  // currently in IF/ID is wrong (fetched from PC+4). It must be flushed.

  when(io.in.pcFromTakenBranch) {
    io.out.IFIDclear := true.B
    // If the branch decision happens in Execute stage, you might need to flush ID/EX too,
    // depending on your design. Usually, if decision is in EX, you flush ID/EX and IF/ID.
    io.out.IDEXclear := true.B
  }
}

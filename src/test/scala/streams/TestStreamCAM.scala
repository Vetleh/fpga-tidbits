package streams

import chisel3._
import chisel3.util._
import org.scalatest._
import chiseltest._

import fpgatidbits.streams._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class TestStreamCAM extends AnyFlatSpec with ChiselScalatestTester with Matchers {

  behavior of "StreamCAM"
  it should "Initalize properly" in {
    test(new CAM(10, 8)){
      c =>
        c.io.hasFree.expect(true.B)
        c.io.freeInd.expect(0.U)
    }
  }
}



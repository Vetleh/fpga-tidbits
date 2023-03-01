package utils

import chisel3._
import chisel3.util._
import chiseltest._
import org.scalatest._
import org.scalatest.freespec.AnyFreeSpec

import fpgatidbits.utils.BitExtraction
import org.scalatest.matchers.should.Matchers

class TestWrapper extends Module {
  val io = IO( new Bundle {
    val word = Input(UInt(32.W))
    val high = Input(UInt(8.W))
    val low = Input(UInt(8.W))
    val result = Output(UInt(32.W))
  })

  io.result := BitExtraction(io.word, io.high, io.low)
}



class TestUtils() extends AnyFreeSpec with ChiselScalatestTester {
  "TestBitExtraction" in {
      test(new TestWrapper()) { c =>
        def TestBitExtraction(in: Int, h: Int, l: Int) = {
          c.io.word.poke(in.U)
          c.io.high.poke(h.U)
          c.io.low.poke(l.U)
          c.clock.step(1)
          c.io.result.expect(in.U(32.W)(h,l))
        }

        TestBitExtraction(123456789,8,1)
        TestBitExtraction(123456789,7,6)
        TestBitExtraction(123456789,32,4)
        TestBitExtraction(123456789,3,0)
      }
    }
}
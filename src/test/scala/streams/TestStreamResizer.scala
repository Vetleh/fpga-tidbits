package fpgatidbits.streams

import chisel3._
import chiseltest._
import org.scalatest.freespec.AnyFreeSpec
import chisel3.experimental.BundleLiterals._

class TestStreamDownSizer extends AnyFreeSpec with ChiselScalatestTester {
  "StreamResizer test" in {
    test(new AXIStreamDownsizer(64, 16)) { c =>
      // simple tester for a 64-to-16 bit downsizer
      c.in.valid.poke(0)
      c.in.bits.poke(0)
      c.out.ready.poke(0)
      c.clock.step(1)
      c.in.ready.expect(1) // ready to pull data
      c.out.valid.expect(0) // no data on output
      // scenario 1:
      // expose a single data beat from in
      c.in.bits.poke("xdeadbeefcafebabe".U(64.W).litValue)
      c.in.valid.poke(1)
      c.clock.step(1)
      c.in.valid.poke(0)
      c.in.ready.expect(0) // no more pull
      c.out.valid.expect(1) // data on output
      c.out.bits.expect("xbabe".U(16.W).litValue)
      c.clock.step(1)
      // data on output stays the same since no ready from out
      c.in.ready.expect(0)
      c.out.valid.expect(1)
      c.out.bits.expect("xbabe".U(16.W).litValue)
      c.out.ready.poke(1)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xcafe".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xbeef".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xdead".U(16.W).litValue)
      c.clock.step(1)
      // since no data on input, should go back to sWaitInput
      c.in.ready.expect(1) // ready to pull data
      c.out.valid.expect(0) // no data on output

      // scenario 2:
      // starts identical to scenario 1,
      // expose another beat right after the first one
      c.in.bits.poke("xdeadbeefcafebabe".U(64.W).litValue)
      c.in.valid.poke(1)
      c.clock.step(1)
      c.in.ready.expect(0) // no more pull
      c.out.valid.expect(1) // data on output
      c.out.ready.poke(1)
      c.out.bits.expect("xbabe".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xcafe".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xbeef".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xdead".U(16.W).litValue)
      c.in.bits.poke("xf00dc0debeadfeed".U(64.W).litValue)
      c.in.valid.poke(1)
      c.in.ready.expect(1) // ready to pull data
      c.clock.step(1)
      c.in.valid.poke(0)
      // should go right to sShift
      c.in.ready.expect(0) // no more parallel load
      c.out.valid.expect(1) // serial data available
      c.out.bits.expect("xfeed".U(16.W).litValue)
      c.out.ready.poke(1)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xbead".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xc0de".U(16.W).litValue)
      c.clock.step(1)
      c.out.valid.expect(1)
      c.out.bits.expect("xf00d".U(16.W).litValue)
      c.clock.step(1)
      // should be back to sWaitInput
      c.in.ready.expect(1) // ready to pull data
      c.out.valid.expect(0) // no data on output
    }
  }
}

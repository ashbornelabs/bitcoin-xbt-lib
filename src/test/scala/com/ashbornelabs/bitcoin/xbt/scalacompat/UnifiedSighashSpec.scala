package com.ashbornelabs.bitcoin.xbt.scalacompat

import com.ashbornelabs.bitcoin.xbt.SigVersion
import org.json4s.JsonAST.{JArray, JInt, JString}
import org.json4s.jackson.JsonMethods
import org.json4s.{DefaultFormats, JValue}
import org.scalatest.FunSuite
import scodec.bits.ByteVector

import java.io.InputStreamReader

class UnifiedSighashSpec extends FunSuite {
  implicit val format: DefaultFormats.type = DefaultFormats

  private def readJson(resource: String): JValue = JsonMethods.parse(new InputStreamReader(classOf[UnifiedSighashSpec].getResourceAsStream(resource)))

  /**
   * Test vectors from Bitcoin Knots `src/test/data/unified_sighash.json` (tag v29.4.3.knots20260508). Each vector is
   * [scriptCode, rawTx (no witness), inIdx, hashType, scriptType, spentOutputs, sighash], where sighash holds the raw
   * hash bytes. Tapscript vectors have no annex and no OP_CODESEPARATOR.
   */
  test("knots unified sighash test vectors") {
    val sigVersions = Seq(SigVersion.SIGVERSION_BASE, SigVersion.SIGVERSION_WITNESS_V0, SigVersion.SIGVERSION_TAPROOT, SigVersion.SIGVERSION_TAPSCRIPT)
    val checked = Array.fill(sigVersions.size)(0)
    // use tail to skip the first line of the .json file
    readJson("/data/unified_sighash.json").extract[List[List[JValue]]].tail.foreach {
      case JString(scriptCode) :: JString(rawTx) :: JInt(inputIndex) :: JInt(sighashType) :: JInt(scriptType) :: JArray(spent) :: JString(expected) :: Nil =>
        val spentOutputs = spent.map {
          case JArray(JInt(amount) :: JString(publicKeyScript) :: Nil) => TxOut(Satoshi(amount.toLong), ByteVector.fromValidHex(publicKeyScript))
          case unexpected => fail(s"cannot parse spent output $unexpected")
        }
        val sigVersion = sigVersions(scriptType.toInt)
        val tapleaf_opt = if (sigVersion == SigVersion.SIGVERSION_TAPSCRIPT) Some(ScriptTree.Leaf(ByteVector.fromValidHex(scriptCode)).hash()) else None
        val tx = Transaction.read(rawTx)
        val sighash = Transaction.hashForSigningUnified(tx, inputIndex.toInt, spentOutputs, sighashType.toInt, sigVersion, ByteVector.fromValidHex(scriptCode), tapleaf_opt)
        assert(sighash == ByteVector32.fromValidHex(expected))
        checked(scriptType.toInt) += 1
      case unexpected => fail(s"cannot parse vector $unexpected")
    }
    // bare, segwit v0, taproot and tapscript vectors
    assert(checked.toSeq == Seq(76, 66, 12, 12))
  }
}

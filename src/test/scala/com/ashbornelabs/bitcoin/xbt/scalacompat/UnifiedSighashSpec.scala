package com.ashbornelabs.bitcoin.xbt.scalacompat

import com.ashbornelabs.bitcoin.xbt.scalacompat.Crypto.PrivateKey
import com.ashbornelabs.bitcoin.xbt.{ScriptFlags, SigHash, SigVersion}
import org.json4s.JsonAST.{JArray, JInt, JString}
import org.json4s.jackson.JsonMethods
import org.json4s.{DefaultFormats, JValue}
import org.scalatest.FunSuite
import scodec.bits.ByteVector

import java.io.InputStreamReader
import scala.util.Try

class UnifiedSighashSpec extends FunSuite {
  implicit val format: DefaultFormats.type = DefaultFormats

  private val priv = PrivateKey(ByteVector32.fromValidHex("0101010101010101010101010101010101010101010101010101010101010101"))
  private val pub = priv.publicKey
  private val p2wshScript: Seq[ScriptElt] = OP_PUSHDATA(pub) :: OP_CHECKSIG :: Nil
  private val tapscriptKey = PrivateKey(ByteVector32.fromValidHex("0202020202020202020202020202020202020202020202020202020202020202"))
  private val tapleaf = ScriptTree.Leaf(OP_PUSHDATA(tapscriptKey.xOnlyPublicKey()) :: OP_CHECKSIG :: Nil)

  /** A transaction with one output per script type: P2PKH, P2WPKH, P2WSH, P2TR key path and P2TR script path. */
  private val parentTx = Transaction(
    version = 2,
    txIn = TxIn(OutPoint(TxHash(ByteVector32.Zeroes), 0), ByteVector.empty, TxIn.SEQUENCE_FINAL) :: Nil,
    txOut = Seq(
      TxOut(Satoshi(10_000), Script.pay2pkh(pub)),
      TxOut(Satoshi(20_000), Script.pay2wpkh(pub)),
      TxOut(Satoshi(30_000), Script.pay2wsh(p2wshScript)),
      TxOut(Satoshi(40_000), Script.pay2tr(priv.xOnlyPublicKey(), Crypto.TaprootTweak.KeyPathTweak)),
      TxOut(Satoshi(50_000), Script.pay2tr(priv.xOnlyPublicKey(), tapleaf))
    ),
    lockTime = 0
  )
  private val inputs = parentTx.txOut
  private val unsignedTx = Transaction(
    version = 2,
    txIn = inputs.indices.map(i => TxIn(OutPoint(parentTx, i), ByteVector.empty, TxIn.SEQUENCE_FINAL)),
    txOut = TxOut(Satoshi(140_000), Script.pay2wpkh(pub)) :: Nil,
    lockTime = 0
  )

  private def readJson(resource: String): JValue = JsonMethods.parse(new InputStreamReader(classOf[UnifiedSighashSpec].getResourceAsStream(resource)))

  private def spends(tx: Transaction, inputIndex: Int, flags: Int): Boolean = Try {
    val runner = new Script.Runner(Script.Context(tx, inputIndex, inputs(inputIndex).amount, inputs.toList), flags)
    runner.verifyScripts(tx.txIn(inputIndex).signatureScript, inputs(inputIndex).publicKeyScript, tx.txIn(inputIndex).witness)
  }.getOrElse(false)

  private def signAll(sighashType: Int): Transaction = {
    val tx = unsignedTx
    val sigP2pkh = tx.signInput(0, Script.pay2pkh(pub), sighashType, inputs, SigVersion.SIGVERSION_BASE, priv)
    val sigP2wpkh = tx.signInput(1, Script.pay2pkh(pub), sighashType, inputs, SigVersion.SIGVERSION_WITNESS_V0, priv)
    val sigP2wsh = tx.signInput(2, p2wshScript, sighashType, inputs, SigVersion.SIGVERSION_WITNESS_V0, priv)
    val sigKeyPath = tx.signInputTaprootKeyPath(priv, 3, inputs, sighashType, None)
    val sigScriptPath = tx.signInputTaprootScriptPath(tapscriptKey, 4, inputs, sighashType, tapleaf.hash())
    tx
      .updateSigScript(0, OP_PUSHDATA(sigP2pkh) :: OP_PUSHDATA(pub) :: Nil)
      .updateWitness(1, Script.witnessPay2wpkh(pub, sigP2wpkh))
      .updateWitness(2, ScriptWitness(Seq(sigP2wsh, Script.write(p2wshScript))))
      .updateWitness(3, Script.witnessKeyPathPay2tr(sigKeyPath, sighashType))
      .updateWitness(4, Script.witnessScriptPathPay2tr(priv.xOnlyPublicKey(), tapleaf, ScriptWitness(Seq(sigScriptPath.bytes :+ sighashType.toByte)), tapleaf))
  }

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

  test("sign and verify every script type with the unified sighash") {
    val tx = signAll(SigHash.SIGHASH_ALL_UNIFIED)
    inputs.indices.foreach { i =>
      assert(spends(tx, i, ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS), s"input $i")
      assert(spends(tx, i, ScriptFlags.MANDATORY_SCRIPT_VERIFY_FLAGS), s"input $i")
      // On the SHA256d chain, these signatures are invalid: this is the replay protection.
      assert(!spends(tx, i, ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS & ~ScriptFlags.SCRIPT_VERIFY_UNIFIED_SIGHASH), s"input $i")
    }
    tx.correctlySpends(parentTx :: Nil, ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS)
    // Signatures that do not opt in keep signing the legacy messages and remain valid on both chains.
    val legacyTx = signAll(SigHash.SIGHASH_ALL)
    inputs.indices.foreach { i =>
      assert(spends(legacyTx, i, ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS), s"input $i")
      assert(spends(legacyTx, i, ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS & ~ScriptFlags.SCRIPT_VERIFY_UNIFIED_SIGHASH), s"input $i")
    }
  }

  test("sign ecdsa inputs with every spent output") {
    val scriptCode = Script.pay2pkh(pub)
    val sighashType = SigHash.SIGHASH_ALL_UNIFIED
    assert(isUnified(sighashType) && !isUnified(SigHash.SIGHASH_ALL))
    // The compact signature signs the unified hash, and signInput encodes it followed by the sighash byte.
    val sig = unsignedTx.signInputCompact(1, scriptCode, sighashType, inputs, SigVersion.SIGVERSION_WITNESS_V0, priv)
    val hash = unsignedTx.hashForSigningUnified(1, inputs, sighashType, SigVersion.SIGVERSION_WITNESS_V0, Script.write(scriptCode))
    assert(Crypto.verifySignature(hash, sig, pub))
    assert(Transaction.signInput(unsignedTx, 1, scriptCode, sighashType, inputs, SigVersion.SIGVERSION_WITNESS_V0, priv) == Transaction.encodeWitnessEcdsaSig(sig, sighashType))
    // The overloads that take the amount of a single input cannot sign the unified hash.
    intercept[IllegalArgumentException] {
      unsignedTx.signInput(1, scriptCode, sighashType, inputs(1).amount, SigVersion.SIGVERSION_WITNESS_V0, priv)
    }
  }
}

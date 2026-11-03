// Modified by Ashborne Labs (2026) for the Bitcoin BLAKE2b (XBT) chain. See NOTICE.
package com.ashbornelabs.bitcoin.xbt.scalacompat

import com.ashbornelabs.bitcoin.xbt
import com.ashbornelabs.bitcoin.xbt.scalacompat.Crypto._
import scodec.bits.ByteVector

import java.io.{InputStream, OutputStream}
import scala.jdk.CollectionConverters.{ListHasAsScala, SeqHasAsJava}

object KotlinUtils {

  implicit def kmp2scala(input: xbt.ByteVector32): ByteVector32 = ByteVector32(ByteVector.view(input.toByteArray))

  implicit def scala2kmp(input: ByteVector32): xbt.ByteVector32 = new xbt.ByteVector32(input.toArrayUnsafe)

  implicit def kmp2scala(input: xbt.ByteVector64): ByteVector64 = ByteVector64(ByteVector.view(input.toByteArray))

  implicit def scala2kmp(input: ByteVector64): xbt.ByteVector64 = new xbt.ByteVector64(input.toArrayUnsafe)

  implicit def kmp2scala(input: xbt.ByteVector): ByteVector = ByteVector.view(input.toByteArray)

  implicit def scala2kmp(input: ByteVector): xbt.ByteVector = new xbt.ByteVector(input.toArrayUnsafe)

  implicit def kmp2scala(input: xbt.TxId): TxId = TxId(input.value)

  implicit def scala2kmp(input: TxId): xbt.TxId = new xbt.TxId(input.value)

  implicit def kmp2scala(input: xbt.TxHash): TxHash = TxHash(input.value)

  implicit def scala2kmp(input: TxHash): xbt.TxHash = new xbt.TxHash(input.value)

  implicit def kmp2scala(input: xbt.BlockId): BlockId = BlockId(input.value)

  implicit def scala2kmp(input: BlockId): xbt.BlockId = new xbt.BlockId(input.value)

  implicit def kmp2scala(input: xbt.BlockHash): BlockHash = BlockHash(input.value)

  implicit def scala2kmp(input: BlockHash): xbt.BlockHash = new xbt.BlockHash(input.value)

  implicit def kmp2scala(input: xbt.OutPoint): OutPoint = OutPoint(input.hash, input.index)

  implicit def scala2kmp(input: OutPoint): xbt.OutPoint = new xbt.OutPoint(input.hash, input.index)

  implicit def kmp2scala(input: xbt.ScriptWitness): ScriptWitness = ScriptWitness(input.stack.asScala.toList.map(kmp2scala))

  implicit def scala2kmp(input: ScriptWitness): xbt.ScriptWitness = new xbt.ScriptWitness(input.stack.map(scala2kmp).asJava)

  implicit def kmp2scala(input: xbt.ScriptTree.Leaf): ScriptTree.Leaf = ScriptTree.Leaf(kmp2scala(input.getScript), input.getLeafVersion)

  implicit def scala2kmp(input: ScriptTree.Leaf): xbt.ScriptTree.Leaf = new xbt.ScriptTree.Leaf(scala2kmp(input.script), input.leafVersion)

  implicit def kmp2scala(input: xbt.ScriptTree.Branch): ScriptTree.Branch = ScriptTree.Branch(kmp2scala(input.getLeft), kmp2scala(input.getRight))

  implicit def scala2kmp(input: ScriptTree.Branch): xbt.ScriptTree.Branch = new xbt.ScriptTree.Branch(scala2kmp(input.left), scala2kmp(input.right))

  implicit def kmp2scala(input: xbt.ScriptTree): ScriptTree = input match {
    case branch: xbt.ScriptTree.Branch => kmp2scala(branch)
    case leaf: xbt.ScriptTree.Leaf => kmp2scala(leaf)
    case _ => ??? // this cannot happen, but the compiler cannot know that there aren't other cases
  }

  implicit def scala2kmp(input: ScriptTree): xbt.ScriptTree = input match {
    case leaf: ScriptTree.Leaf => scala2kmp(leaf)
    case branch: ScriptTree.Branch => scala2kmp(branch)
  }

  implicit def kmp2scala(input: xbt.Crypto.TaprootTweak): TaprootTweak = input match {
    case xbt.Crypto.TaprootTweak.KeyPathTweak.INSTANCE => TaprootTweak.KeyPathTweak
    case tweak: xbt.Crypto.TaprootTweak.ScriptPathTweak => TaprootTweak.ScriptPathTweak(kmp2scala(tweak.getMerkleRoot))
  }

  implicit def scala2kmp(input: TaprootTweak): xbt.Crypto.TaprootTweak = input match {
    case TaprootTweak.KeyPathTweak => xbt.Crypto.TaprootTweak.KeyPathTweak.INSTANCE
    case tweak: TaprootTweak.ScriptPathTweak => new xbt.Crypto.TaprootTweak.ScriptPathTweak(scala2kmp(tweak.merkleRoot))
  }

  implicit def kmp2scala(input: xbt.TxIn): TxIn = TxIn(input.outPoint, input.signatureScript, input.sequence, input.witness)

  implicit def scala2kmp(input: Satoshi): xbt.Satoshi = new xbt.Satoshi(input.toLong)

  implicit def kmp2scala(input: xbt.Satoshi): Satoshi = Satoshi(input.toLong)

  implicit def scala2kmp(input: TxIn): xbt.TxIn = new xbt.TxIn(scala2kmp(input.outPoint), input.signatureScript, input.sequence, scala2kmp(input.witness))

  implicit def kmp2scala(input: xbt.TxOut): TxOut = TxOut(input.amount, input.publicKeyScript)

  implicit def scala2kmp(input: TxOut): xbt.TxOut = new xbt.TxOut(input.amount, input.publicKeyScript)

  implicit def kmp2scala(input: xbt.Transaction): Transaction = Transaction(input.version, input.txIn.asScala.toList.map(kmp2scala), input.txOut.asScala.toList.map(kmp2scala), input.lockTime)

  implicit def scala2kmp(input: Transaction): xbt.Transaction = input.kmp

  implicit def kmp2scala(input: xbt.PrivateKey): PrivateKey = PrivateKey(input)

  implicit def scala2kmp(input: PrivateKey): xbt.PrivateKey = input.priv

  implicit def kmp2scala(input: xbt.PublicKey): PublicKey = PublicKey(input)

  implicit def scala2kmp(input: PublicKey): xbt.PublicKey = input.pub

  implicit def kmp2scala(input: xbt.XonlyPublicKey): XonlyPublicKey = XonlyPublicKey(input)

  implicit def scala2kmp(input: XonlyPublicKey): xbt.XonlyPublicKey = input.pub

  implicit def kmp2scala(input: xbt.DeterministicWallet.ExtendedPrivateKey): DeterministicWallet.ExtendedPrivateKey = DeterministicWallet.ExtendedPrivateKey(input)

  implicit def scala2kmp(input: DeterministicWallet.ExtendedPrivateKey): xbt.DeterministicWallet.ExtendedPrivateKey = input.priv

  implicit def kmp2scala(input: xbt.DeterministicWallet.ExtendedPublicKey): DeterministicWallet.ExtendedPublicKey = DeterministicWallet.ExtendedPublicKey(input)

  implicit def scala2kmp(input: DeterministicWallet.ExtendedPublicKey): xbt.DeterministicWallet.ExtendedPublicKey = input.pub

  implicit def kmp2scala(input: xbt.KeyPath): DeterministicWallet.KeyPath = DeterministicWallet.KeyPath(input)

  implicit def scala2kmp(input: DeterministicWallet.KeyPath): xbt.KeyPath = input.keyPath

  case class InputStreamWrapper(is: InputStream) extends xbt.io.Input {
    // NB: on the JVM we will use a ByteArrayInputStream, which guarantees that the result will be correct.
    override def getAvailableBytes: Int = is.available()

    override def read(): Int = is.read()

    override def read(bytes: Array[Byte], i: Int, i1: Int): Int = is.read(bytes, i, i1)
  }

  case class OutputStreamWrapper(os: OutputStream) extends xbt.io.Output {
    override def write(bytes: Array[Byte], i: Int, i1: Int): Unit = os.write(bytes, i, i1)

    override def write(i: Int): Unit = os.write(i)
  }

  implicit def eitherkmp2either[L, R](input: com.ashbornelabs.bitcoin.xbt.utils.Either[L, R]): Either[L, R] = if (input.isLeft) Left(input.getLeft) else Right(input.getRight)

  implicit def either2keitherkmp[L, R](input: Either[L, R]): com.ashbornelabs.bitcoin.xbt.utils.Either[L, R] = input match {
    case Left(l) => new com.ashbornelabs.bitcoin.xbt.utils.Either.Left[L](l).asInstanceOf[com.ashbornelabs.bitcoin.xbt.utils.Either[L, R]]
    case Right(r) => new com.ashbornelabs.bitcoin.xbt.utils.Either.Right[R](r).asInstanceOf[com.ashbornelabs.bitcoin.xbt.utils.Either[L, R]]
  }

  implicit def scala2kmp(input: ScriptElt): xbt.ScriptElt = input match {
    case OP_PUSHDATA(data, _) => new xbt.OP_PUSHDATA(data)
    case _ => scriptEltMapScala2Kmp(input)
  }

  implicit def kmp2scala(input: xbt.ScriptElt): ScriptElt = input match {
    case oppushdata: xbt.OP_PUSHDATA => OP_PUSHDATA(oppushdata.data, oppushdata.opCode)
    case _ => scriptEltMapKmp2Scala2Map(input)
  }

  private val scriptEltMapScala2Kmp: Map[ScriptElt, xbt.ScriptElt] = Map(
    OP_0 -> xbt.OP_0.INSTANCE,
    OP_1NEGATE -> xbt.OP_1NEGATE.INSTANCE,
    OP_RESERVED -> xbt.OP_RESERVED.INSTANCE,
    OP_1 -> xbt.OP_1.INSTANCE,
    OP_2 -> xbt.OP_2.INSTANCE,
    OP_3 -> xbt.OP_3.INSTANCE,
    OP_4 -> xbt.OP_4.INSTANCE,
    OP_5 -> xbt.OP_5.INSTANCE,
    OP_6 -> xbt.OP_6.INSTANCE,
    OP_7 -> xbt.OP_7.INSTANCE,
    OP_8 -> xbt.OP_8.INSTANCE,
    OP_9 -> xbt.OP_9.INSTANCE,
    OP_10 -> xbt.OP_10.INSTANCE,
    OP_11 -> xbt.OP_11.INSTANCE,
    OP_12 -> xbt.OP_12.INSTANCE,
    OP_13 -> xbt.OP_13.INSTANCE,
    OP_14 -> xbt.OP_14.INSTANCE,
    OP_15 -> xbt.OP_15.INSTANCE,
    OP_16 -> xbt.OP_16.INSTANCE,
    OP_NOP -> xbt.OP_NOP.INSTANCE,
    OP_VER -> xbt.OP_VER.INSTANCE,
    OP_IF -> xbt.OP_IF.INSTANCE,
    OP_NOTIF -> xbt.OP_NOTIF.INSTANCE,
    OP_VERIF -> xbt.OP_VERIF.INSTANCE,
    OP_VERNOTIF -> xbt.OP_VERNOTIF.INSTANCE,
    OP_ELSE -> xbt.OP_ELSE.INSTANCE,
    OP_ENDIF -> xbt.OP_ENDIF.INSTANCE,
    OP_VERIFY -> xbt.OP_VERIFY.INSTANCE,
    OP_RETURN -> xbt.OP_RETURN.INSTANCE,
    OP_TOALTSTACK -> xbt.OP_TOALTSTACK.INSTANCE,
    OP_FROMALTSTACK -> xbt.OP_FROMALTSTACK.INSTANCE,
    OP_2DROP -> xbt.OP_2DROP.INSTANCE,
    OP_2DUP -> xbt.OP_2DUP.INSTANCE,
    OP_3DUP -> xbt.OP_3DUP.INSTANCE,
    OP_2OVER -> xbt.OP_2OVER.INSTANCE,
    OP_2ROT -> xbt.OP_2ROT.INSTANCE,
    OP_2SWAP -> xbt.OP_2SWAP.INSTANCE,
    OP_IFDUP -> xbt.OP_IFDUP.INSTANCE,
    OP_DEPTH -> xbt.OP_DEPTH.INSTANCE,
    OP_DROP -> xbt.OP_DROP.INSTANCE,
    OP_DUP -> xbt.OP_DUP.INSTANCE,
    OP_NIP -> xbt.OP_NIP.INSTANCE,
    OP_OVER -> xbt.OP_OVER.INSTANCE,
    OP_PICK -> xbt.OP_PICK.INSTANCE,
    OP_ROLL -> xbt.OP_ROLL.INSTANCE,
    OP_ROT -> xbt.OP_ROT.INSTANCE,
    OP_SWAP -> xbt.OP_SWAP.INSTANCE,
    OP_TUCK -> xbt.OP_TUCK.INSTANCE,
    OP_CAT -> xbt.OP_CAT.INSTANCE,
    OP_SUBSTR -> xbt.OP_SUBSTR.INSTANCE,
    OP_LEFT -> xbt.OP_LEFT.INSTANCE,
    OP_RIGHT -> xbt.OP_RIGHT.INSTANCE,
    OP_SIZE -> xbt.OP_SIZE.INSTANCE,
    OP_INVERT -> xbt.OP_INVERT.INSTANCE,
    OP_AND -> xbt.OP_AND.INSTANCE,
    OP_OR -> xbt.OP_OR.INSTANCE,
    OP_XOR -> xbt.OP_XOR.INSTANCE,
    OP_EQUAL -> xbt.OP_EQUAL.INSTANCE,
    OP_EQUALVERIFY -> xbt.OP_EQUALVERIFY.INSTANCE,
    OP_RESERVED1 -> xbt.OP_RESERVED1.INSTANCE,
    OP_RESERVED2 -> xbt.OP_RESERVED2.INSTANCE,
    OP_1ADD -> xbt.OP_1ADD.INSTANCE,
    OP_1SUB -> xbt.OP_1SUB.INSTANCE,
    OP_2MUL -> xbt.OP_2MUL.INSTANCE,
    OP_2DIV -> xbt.OP_2DIV.INSTANCE,
    OP_NEGATE -> xbt.OP_NEGATE.INSTANCE,
    OP_ABS -> xbt.OP_ABS.INSTANCE,
    OP_NOT -> xbt.OP_NOT.INSTANCE,
    OP_0NOTEQUAL -> xbt.OP_0NOTEQUAL.INSTANCE,
    OP_ADD -> xbt.OP_ADD.INSTANCE,
    OP_SUB -> xbt.OP_SUB.INSTANCE,
    OP_MUL -> xbt.OP_MUL.INSTANCE,
    OP_DIV -> xbt.OP_DIV.INSTANCE,
    OP_MOD -> xbt.OP_MOD.INSTANCE,
    OP_LSHIFT -> xbt.OP_LSHIFT.INSTANCE,
    OP_RSHIFT -> xbt.OP_RSHIFT.INSTANCE,
    OP_BOOLAND -> xbt.OP_BOOLAND.INSTANCE,
    OP_BOOLOR -> xbt.OP_BOOLOR.INSTANCE,
    OP_NUMEQUAL -> xbt.OP_NUMEQUAL.INSTANCE,
    OP_NUMEQUALVERIFY -> xbt.OP_NUMEQUALVERIFY.INSTANCE,
    OP_NUMNOTEQUAL -> xbt.OP_NUMNOTEQUAL.INSTANCE,
    OP_LESSTHAN -> xbt.OP_LESSTHAN.INSTANCE,
    OP_GREATERTHAN -> xbt.OP_GREATERTHAN.INSTANCE,
    OP_LESSTHANOREQUAL -> xbt.OP_LESSTHANOREQUAL.INSTANCE,
    OP_GREATERTHANOREQUAL -> xbt.OP_GREATERTHANOREQUAL.INSTANCE,
    OP_MIN -> xbt.OP_MIN.INSTANCE,
    OP_MAX -> xbt.OP_MAX.INSTANCE,
    OP_WITHIN -> xbt.OP_WITHIN.INSTANCE,
    OP_RIPEMD160 -> xbt.OP_RIPEMD160.INSTANCE,
    OP_SHA1 -> xbt.OP_SHA1.INSTANCE,
    OP_SHA256 -> xbt.OP_SHA256.INSTANCE,
    OP_HASH160 -> xbt.OP_HASH160.INSTANCE,
    OP_HASH256 -> xbt.OP_HASH256.INSTANCE,
    OP_CODESEPARATOR -> xbt.OP_CODESEPARATOR.INSTANCE,
    OP_CHECKSIG -> xbt.OP_CHECKSIG.INSTANCE,
    OP_CHECKSIGADD -> xbt.OP_CHECKSIGADD.INSTANCE,
    OP_CHECKSIGVERIFY -> xbt.OP_CHECKSIGVERIFY.INSTANCE,
    OP_CHECKMULTISIG -> xbt.OP_CHECKMULTISIG.INSTANCE,
    OP_CHECKMULTISIGVERIFY -> xbt.OP_CHECKMULTISIGVERIFY.INSTANCE,
    OP_NOP1 -> xbt.OP_NOP1.INSTANCE,
    OP_CHECKLOCKTIMEVERIFY -> xbt.OP_CHECKLOCKTIMEVERIFY.INSTANCE,
    OP_CHECKSEQUENCEVERIFY -> xbt.OP_CHECKSEQUENCEVERIFY.INSTANCE,
    OP_NOP4 -> xbt.OP_NOP4.INSTANCE,
    OP_NOP5 -> xbt.OP_NOP5.INSTANCE,
    OP_NOP6 -> xbt.OP_NOP6.INSTANCE,
    OP_NOP7 -> xbt.OP_NOP7.INSTANCE,
    OP_NOP8 -> xbt.OP_NOP8.INSTANCE,
    OP_NOP9 -> xbt.OP_NOP9.INSTANCE,
    OP_NOP10 -> xbt.OP_NOP10.INSTANCE,
    OP_INVALIDOPCODE -> xbt.OP_INVALIDOPCODE.INSTANCE)

  private val scriptEltMapKmp2Scala2Map: Map[xbt.ScriptElt, ScriptElt] = scriptEltMapScala2Kmp.map { case (k, v) => v -> k }

}


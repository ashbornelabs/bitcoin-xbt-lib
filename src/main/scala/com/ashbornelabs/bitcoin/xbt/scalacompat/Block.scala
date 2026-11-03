// Modified by Ashborne Labs (2026) for the Bitcoin BLAKE2b (XBT) chain. See NOTICE.
package com.ashbornelabs.bitcoin.xbt.scalacompat

import KotlinUtils._

/** This is the double hash of a serialized block header. */
case class BlockHash(value: ByteVector32) {
  override def toString = value.toString
}

object BlockHash {
  def apply(blockId: BlockId): BlockHash = BlockHash(blockId.value.reverse)
}

/** This contains the same data as [BlockHash], but encoded with the opposite endianness. */
case class BlockId(value: ByteVector32) {
  override def toString = value.toString
}

object BlockId {
  def apply(blockHash: BlockHash): BlockId = BlockId(blockHash.value.reverse)
}

object Block {
  // genesis blocks
  object LivenetGenesisBlock {
    val blockId: BlockId = com.ashbornelabs.bitcoin.xbt.Block.LivenetGenesisBlock.blockId
    val hash: BlockHash = com.ashbornelabs.bitcoin.xbt.Block.LivenetGenesisBlock.hash
  }

  object Testnet4GenesisBlock {
    val blockId: BlockId = com.ashbornelabs.bitcoin.xbt.Block.Testnet4GenesisBlock.blockId
    val hash: BlockHash = com.ashbornelabs.bitcoin.xbt.Block.Testnet4GenesisBlock.hash
  }

  object RegtestGenesisBlock {
    val blockId: BlockId = com.ashbornelabs.bitcoin.xbt.Block.RegtestGenesisBlock.blockId
    val hash: BlockHash = com.ashbornelabs.bitcoin.xbt.Block.RegtestGenesisBlock.hash
  }
}
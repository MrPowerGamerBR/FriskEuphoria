package com.mrpowergamerbr.friskeuphoria.utils

import com.google.common.io.LittleEndianDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File

class StringChunk(chunkName: String) : Chunk(chunkName) {
	var addressCount: Int = 0
	var addresses = mutableListOf<Int>()
	var strings = mutableListOf<String>()
	var currentAddr = 13429408
	var originalAddrs = mutableListOf<Int>()
	var newAddrs = mutableListOf<Int>()

	lateinit var unknown: ByteArray

	override fun write(dos: LittleEndianDataOutputStream) {
		dos.writeInt(addressCount)

		for ((index, string) in strings.withIndex()) {
			newAddrs.add(currentAddr)
			dos.writeInt(currentAddr)
			currentAddr = currentAddr + 4 + string.length + 1
		}

		for (string in strings) {
			dos.writeInt(string.length)
			for (char in string) {
				dos.writeByte(char.toInt())
			}
			dos.writeByte(0) // Null termination
		}

		dos.write(unknown)
	}

	override fun read(binaryStream: BinaryStream) {
		var chunkLength = binaryStream.buffer.int

		var chunkEnd = binaryStream.buffer.position() + chunkLength

		var addressCount = binaryStream.buffer.int

		this.addressCount = addressCount

		println("addressCount: $addressCount")

		for (i in 0 until addressCount) {
			val addr = binaryStream.buffer.int
			addresses.add(addr)
			originalAddrs.add(addr)
		}

		val test = mutableListOf<String>()

		// val strings = mutableListOf<String>()
		for (i in 0 until addressCount) {
			var length = binaryStream.buffer.int

			val str = binaryStream.readString(length)

			strings.add(str.replace("Long", "oao "))

			binaryStream.buffer.get() // unknown byte
		}

		test.add("AddrCount: " + addressCount)

		for ((index, string) in strings.withIndex()) {
			test.add(addresses[index].toString() + " (${string.length}) ~ " + string)
		}
		val string = test.joinToString(separator = "\n")

		File("D:\\FriskEuphoria\\strings.txt").writeText(string)

		val baos = ByteArrayOutputStream()

		println("Chunk End: $chunkEnd")
		println("Pos: ${binaryStream.buffer.position()}")
		while (chunkEnd > binaryStream.buffer.position()) {
			baos.write(binaryStream.buffer.get().toInt())
		}

		unknown = baos.toByteArray()
		// binaryStream.buffer.position(chunkEnd)
	}

	override fun size(): Int {
		// 2857768
		var size = 8 // FORM + Chunk length (4 bytes + int)
		size += 4 // AddressCount (int)
		for (address in addresses) {
			size += 4 // Addresses (int)
		}
		for (string in strings) {
			size += 4 // String length (int)
			size += string.length // Length
			size += 1 // Null terminator (byte)
		}
		size += unknown.size // (???)
		return size
	}
}
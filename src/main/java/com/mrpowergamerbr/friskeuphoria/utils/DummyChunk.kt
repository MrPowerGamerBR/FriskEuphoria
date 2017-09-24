package com.mrpowergamerbr.friskeuphoria.utils

import com.google.common.io.LittleEndianDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class DummyChunk(chunkName: String) : Chunk(chunkName) {
	lateinit var byteArray: ByteArray
	var isEmpty = false

	override fun write(dos: LittleEndianDataOutputStream) {
		dos.write(byteArray)
	}

	override fun read(binaryStream: BinaryStream) {
		var chunkLength = binaryStream.buffer.int
		val baos = ByteArrayOutputStream()
		for (i in 0 until chunkLength) {
			baos.write(binaryStream.buffer.get().toInt())
		}
		byteArray = baos.toByteArray()
		// binaryStream.skip(chunkLength) // Skip section
		println("Skipping $chunkName ~ Length: $chunkLength...")
	}

	override fun size(): Int {
		if (isEmpty) {
			return 12
		}
		return byteArray.size + 8 // 4 = chunk name; 4 = chunk length
	}
}
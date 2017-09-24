package com.mrpowergamerbr.friskeuphoria

import com.google.common.io.LittleEndianDataOutputStream
import com.mrpowergamerbr.friskeuphoria.utils.BinaryStream
import com.mrpowergamerbr.friskeuphoria.utils.Chunk
import com.mrpowergamerbr.friskeuphoria.utils.DummyChunk
import com.mrpowergamerbr.friskeuphoria.utils.StringChunk
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun main(args: Array<String>) {
	val bytes = File("D:\\SteamLibrary\\steamapps\\common\\Undertale\\data_original.win").readBytes() // And read it to a byte array!
	val edited = File("D:\\SteamLibrary\\steamapps\\common\\Undertale\\data.win")

	var binaryStream = BinaryStream(bytes)

	binaryStream.buffer.order(ByteOrder.LITTLE_ENDIAN) // Needs to be little endian

	var chunkName = binaryStream.readString(4) // All chunk names are 4 bytes long

	println(chunkName)

	var formLength = binaryStream.buffer.int

	var formPos = 0;

	println("FORM header is $formLength bytes long")

	println("FORM sub-chunks:")

	// List all available chunks to console
	while (formLength > formPos) {
		var chunkName = binaryStream.readString(4)
		var chunkLength = binaryStream.buffer.int

		formPos += 8;

		var txt = " - " + chunkName;

		if (chunkLength == 4) {
			txt += " (empty)"
		}
		println(txt)

		binaryStream.skip(chunkLength) // Skip section
		formPos += chunkLength
	}

	binaryStream.buffer.position(8) // Reset the position to idx 8 (after the FORM header)

	val chunks = mutableListOf<Chunk>()

	// Process all chunks within FORM
	while (binaryStream.buffer.hasRemaining()) {
		var chunkName = binaryStream.readString(4)

		val chunk = when (chunkName) {
			"STRG" -> StringChunk(chunkName)
			else -> DummyChunk(chunkName)
		}
		chunk.read(binaryStream)

		chunks.add(chunk)
	}

	edited.delete()

	var newFormSize = 0

	for (chunk in chunks) {
		newFormSize += chunk.size()
	}

	println("Old FORM header is $formLength bytes long")
	println("New FORM header is $newFormSize bytes long")

	val baos = ByteArrayOutputStream()
	val dos = LittleEndianDataOutputStream(baos)
	dos.writeByte('F'.toInt())
	dos.writeByte('O'.toInt())
	dos.writeByte('R'.toInt())
	dos.writeByte('M'.toInt())
	dos.writeInt(newFormSize)

	for (chunk in chunks) {
		dos.writeByte(chunk.chunkName[0].toInt())
		dos.writeByte(chunk.chunkName[1].toInt())
		dos.writeByte(chunk.chunkName[2].toInt())
		dos.writeByte(chunk.chunkName[3].toInt())
		dos.writeInt(chunk.size() - 8)
		chunk.write(dos)
	}


	println("Length: " + baos.toByteArray().size)

	edited.writeBytes(baos.toByteArray())
	// edited.writeText("wow")
}
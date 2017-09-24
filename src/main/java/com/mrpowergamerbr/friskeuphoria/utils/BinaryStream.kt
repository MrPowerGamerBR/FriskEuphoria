package com.mrpowergamerbr.friskeuphoria.utils

import java.nio.ByteBuffer

// From TemmieSC2KParser: https://github.com/MrPowerGamerBR/TemmieSC2KParser/blob/master/src/main/java/com/mrpowergamerbr/temmiesc2kparser/utils/SC2KBinaryStream.kt
class BinaryStream {
	val buffer: ByteBuffer;

	constructor(byteArray: ByteArray) {
		this.buffer = ByteBuffer.wrap(byteArray)
	}

	// Read string from ByteBuffer
	fun readString(length: Int): String {
		var str = "";
		for (i in buffer.position()..buffer.position() + length - 1) {
			str += buffer.get().toChar();
		}
		return str;
	}

	fun skip(skip: Int) {
		buffer.position(buffer.position() + skip)
	}
}
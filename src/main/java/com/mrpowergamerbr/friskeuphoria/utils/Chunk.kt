package com.mrpowergamerbr.friskeuphoria.utils

import com.google.common.io.LittleEndianDataOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

abstract class Chunk(val chunkName: String) {
	abstract fun read(binaryStream: BinaryStream)

	abstract fun write(dos: LittleEndianDataOutputStream)

	abstract fun size(): Int
}
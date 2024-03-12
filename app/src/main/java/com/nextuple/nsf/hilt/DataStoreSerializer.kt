package com.nextuple.nsf.hilt

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.nextuple.nsf.Printers
import com.nextuple.nsf.UserData
import java.io.InputStream
import java.io.OutputStream

@Suppress("BlockingMethodInNonBlockingContext")
object PrintersSerializer : Serializer<Printers> {
	override val defaultValue: Printers = Printers.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): Printers {
		try {
			return Printers.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		} catch (e: java.io.IOException) {
			e.printStackTrace()
			throw e
		}
	}

	override suspend fun writeTo(t: Printers, output: OutputStream) = t.writeTo(output)
}

@Suppress("BlockingMethodInNonBlockingContext")
object UserSerializer : Serializer<UserData> {
	override val defaultValue: UserData = UserData.getDefaultInstance()

	override suspend fun readFrom(input: InputStream): UserData {
		try {
			return UserData.parseFrom(input)
		} catch (exception: InvalidProtocolBufferException) {
			throw CorruptionException("Cannot read proto.", exception)
		} catch (e: java.io.IOException) {
			e.printStackTrace()
			throw e
		}
	}

	override suspend fun writeTo(t: UserData, output: OutputStream) = t.writeTo(output)
}

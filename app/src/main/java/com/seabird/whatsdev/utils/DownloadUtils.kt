package com.seabird.whatsdev.utils

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object DownloadUtils {
    /**
     * Copy stream from the input stream.
     *
     * @param input  Instance of InputStream
     * @param output Instance of OutputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }
}
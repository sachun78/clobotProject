package kr.co.clobot.robot.common.main.util

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class FileUtil {
    companion object {
        @Throws(IOException::class)
        fun copyFile(`in`: InputStream, out: OutputStream) {
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
        }

        fun deleteFile(path: String?) {
            val file = File(path)
            val deleted: Boolean = file.delete()
        }
    }
}
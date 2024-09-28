package xyz.droidev.eventsync.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import xyz.droidev.eventsync.R
import java.io.File

/**
 * Project : Healthcare.
 * @author PANDEY ANURAG.
 */
class FileProvider: FileProvider(R.xml.file_path){

    companion object{
        fun getImageUri(context: Context): Uri {
            val directory = File(context.filesDir, "images")
            directory.mkdirs()
            val file = File.createTempFile("${System.currentTimeMillis()}", ".jpg", directory)
            val authority = "${context.packageName}.fileprovider"
            return getUriForFile(context, authority, file)
        }
    }
}
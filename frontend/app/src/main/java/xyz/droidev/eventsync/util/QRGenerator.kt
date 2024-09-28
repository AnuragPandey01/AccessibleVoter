package xyz.droidev.eventsync.util

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlin.jvm.Throws

/*
* Utility class to generate QR code bitmap from text
* */
object QRGenerator {

    /**
     * Generate QR code bitmap from text
     * @param text: Text to be encoded in QR code
     * @param width: Width of the QR code bitmap
     * @param height: Height of the QR code bitmap
     * @return Bitmap: QR code bitmap
     * @throws WriterException: If the QR code writer fails to encode the text
     * */
    @Throws(WriterException::class)
    fun generateQRCodeBitmap(text: String, width: Int, height: Int): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)

        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height

        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)

        for (x in 0 until bitMatrixWidth) {
            for (y in 0 until bitMatrixHeight) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        return bitmap
    }

}
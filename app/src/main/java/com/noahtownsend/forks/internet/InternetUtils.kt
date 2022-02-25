package com.noahtownsend.forks.internet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class InternetUtils {
    companion object {
        suspend fun get(
            url: String,
            requestHeaders: Map<String, String> = HashMap()
        ): Pair<String, Map<String, List<String>>> {
            val buffer = StringBuffer()
            var responseHeaders: Map<String, List<String>> = HashMap()

            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    val connection = getConnection(url, requestHeaders)
                    val bufferedReader =
                        BufferedReader(InputStreamReader(connection.inputStream))
                    var line = bufferedReader.readLine()

                    while (line != null) {
                        buffer.append(line)
                        line = bufferedReader.readLine()
                    }

                    responseHeaders = connection.headerFields
                }.onFailure { e ->
                    throw e
                }
            }
            return Pair(buffer.toString(), responseHeaders)
        }

        suspend fun getBitmapFromURL(url: String): Bitmap? {
            var bitmap: Bitmap? = null

            withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    val connection = getConnection(url)
                    bitmap = BitmapFactory.decodeStream(connection.inputStream)
                }
            }.onFailure { e ->
                throw e
            }

            return bitmap
        }

        private fun getConnection(
            url: String,
            requestHeaders: Map<String, String> = HashMap()
        ): HttpURLConnection {
            val urlObj = URL(url)
            val connection = (urlObj.openConnection() as HttpURLConnection).apply {
                doInput = true
            }

            for ((key, value) in requestHeaders.entries) {
                connection.addRequestProperty(key, value)
            }

            connection.connect()

            return connection

        }
    }
}
package com.dianhun.common

import java.io.IOException
import java.io.InputStreamReader
import java.io.BufferedReader
import java.io.PrintWriter
import java.net.URL


object Http{
    private fun readAll(bfr: BufferedReader, result:String) :String {
        val line = bfr.readLine() ?: return result
        return readAll(bfr, result + line)
    }

    fun sendPost(url: String, param: String): String {
        var out: PrintWriter? = null
        var `in`: BufferedReader? = null
        var result = ""
        try {
            val realUrl = URL(url)
            val conn = realUrl.openConnection()
            conn.setRequestProperty("accept", "*/*")
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true)
            conn.setDoInput(true)
            out = PrintWriter(conn.getOutputStream())
            out!!.print(param)
            out.flush()
            `in` = BufferedReader(
                    InputStreamReader(conn.getInputStream()))
//            var line: String
            result = readAll(`in`,"")
//            while ((line = `in`.readLine()) != null) {
//                result += line
//            }
        } catch (e: Exception) {
            return ""
        } finally {
            try {
                if (out != null) {
                    out.close()
                }
                if (`in` != null) {
                    `in`.close()
                }
            } catch (e: IOException) {
                return ""
            }

        }
        return result
    }
}
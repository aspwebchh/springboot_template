package com.dianhun.common

data class ServerResult(val code: Int, val message:String, val url: String? = null) {
    companion object {
        val SUCCESS = 0
        val FILE_INVALID = 1
        val ERROR = 2
    }
}

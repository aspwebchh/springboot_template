package com.dianhun.common

data class UserInfoFromCookie(val userName: String, val name: String, var userKey: String) {
    companion object {
        val empty = UserInfoFromCookie("","","")
    }
}
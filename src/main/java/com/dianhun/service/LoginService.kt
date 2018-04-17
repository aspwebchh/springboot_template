package com.dianhun.service

import com.alibaba.fastjson.JSON
import com.dianhun.common.Common
import com.dianhun.common.Contanst
import com.dianhun.common.Http
import com.dianhun.common.UserInfoFromCookie
import com.dianhun.config.AppConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.net.URLDecoder
import java.net.URLEncoder
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class LoginService {
    @Autowired
    val appConfig : AppConfig? = null

    @Autowired
    val response : HttpServletResponse? = null

    @Autowired
    val request : HttpServletRequest? = null

    fun login(key: String) :Pair<Boolean, String>{
        val url = Contanst.LOGIN_CHECK_URL +  "?id=" + appConfig!!.appId
        var result = Http.sendPost(url, "key=$key&request=sms")
        val data = JSON.parseObject(result)
        val success = data.getBoolean("success")
        if(!success) {
            return Pair(false,"登录状态过期")
        }
        val userName = data.getString("user_name")
        val name =  data.getString("name")
        val userKey = createUserKey(userName, name)

        val nameCookie = createCookie("name",name )
        val userNameCookie = createCookie("user_name", userName)
        val userKeyCookie = createCookie("user_key", userKey)
        response!!.addCookie(nameCookie)
        response!!.addCookie(userNameCookie)
        response!!.addCookie(userKeyCookie)

        return Pair(true,"登录成功")
    }

    fun createCookie( key: String, value : String ) : Cookie{
        val cookie = Cookie(key, URLEncoder.encode( value ))
        cookie.path = "/"
        cookie.maxAge = -1
        return cookie
    }

    private fun createUserKey( userName: String, name:String): String {
        val key =  userName + name + "dfdj_video_manager_9527"//上线上应再加上sessionid
        return Common.MD5(key)
    }

    private fun findInCookie(key: String): String {
        val cookies = request!!.cookies
        if( cookies == null || cookies.isEmpty()) {
            return ""
        }
        val value = cookies.indices
                .map { cookies[it] }
                .firstOrNull { it.name.compareTo(key) == 0 }
                ?.value ?: ""
        return URLDecoder.decode(value)
    }

    fun isLogin() : Boolean {
        val userInfo = getUserInfoFromCookie()
        val oldUserKey = userInfo.userKey
        val newUserKey = createUserKey( userInfo.userName, userInfo.name )
        return oldUserKey == newUserKey
    }

    private fun getUserInfoFromCookie() : UserInfoFromCookie {
        val name = findInCookie("name")
        val userName = findInCookie("user_name")
        val oldUserKey = findInCookie("user_key")
        return UserInfoFromCookie(userName, name, oldUserKey)
    }

    fun removeAllCookie() {
        val cookies = request!!.cookies ?: return
        for (cookie in cookies) {
            cookie.maxAge = 0
            cookie.path = "/"
            response!!.addCookie(cookie)
        }
    }

    fun userInfo() : UserInfoFromCookie {
        if( !isLogin()) {
            return UserInfoFromCookie.empty
        }
        val userInfo = getUserInfoFromCookie()
        userInfo.userKey = ""
        return userInfo
    }
}
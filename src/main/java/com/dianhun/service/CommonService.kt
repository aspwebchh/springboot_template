package com.dianhun.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class CommonService {
    @Autowired
    private val request: HttpServletRequest? = null

    fun getDomain(): String {
        val url = request?.scheme + "://" + request?.serverName + ":" + request?.serverPort
        return url
    }
}
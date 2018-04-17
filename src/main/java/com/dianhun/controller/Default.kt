package com.dianhun.controller

import com.dianhun.service.VideoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.PrintWriter
import com.dianhun.common.Common
import com.dianhun.common.Contanst
import com.dianhun.config.AppConfig
import com.dianhun.service.CommonService
import com.dianhun.service.LoginService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.ResponseEntity
import java.net.URLEncoder
import javax.servlet.http.HttpServletResponse


@Controller
class Default {
    @Autowired
    val videoService: VideoService? = null

    @Autowired
    val commonService: CommonService? = null

    @Autowired
    val response : HttpServletResponse? = null

    @Autowired
    val loginService : LoginService? = null

    @Autowired
    val appConfig : AppConfig? = null


    fun resultString(result: Any): String {
        return Common.resultString(result)
    }

    @RequestMapping(value = "/upload", method = arrayOf(RequestMethod.POST))
    @ResponseBody
    fun upload(pw: PrintWriter, @RequestParam("video") video: MultipartFile, @RequestParam("uid") uid: String) {
        if(videoService == null) {
            return
        }
        val result = videoService.save( uid,video )
        pw.print(resultString(result))
    }

    @RequestMapping("/download")
    fun download( id : Int ): ResponseEntity<FileSystemResource>? {
        return videoService?.readFile( id )
    }

    @RequestMapping("/notify")
    fun notify( pw: PrintWriter, url: String)  {
        videoService?.let {
            val result = videoService.notify(url)
            pw.print(resultString(result))
        }
    }

    @RequestMapping("/login_callback")
    fun loginCallback(key:String, pw : PrintWriter) {
        val(success, message) = loginService!!.login(key)
        if( success ) {
            response!!.sendRedirect("/admin/index.html")
        } else {
            pw.println( "The login status is out of date <br/> <a href='/login'>log in</a>")
        }
        response!!.contentType = "text/html"
    }

    @RequestMapping("/login")
    fun login( pw:PrintWriter) {
        val domain = commonService!!.getDomain()
        val callbackUrl = domain + "/login_callback"
        val amsLoginPage = Contanst.LOGIN_URL + "?id="+ appConfig!!.appId +"&url=" + URLEncoder.encode(callbackUrl)
        response!!.sendRedirect(amsLoginPage)
    }

    @RequestMapping("/logout")
    fun logout() {
        loginService!!.removeAllCookie()
        response!!.sendRedirect("/login")
    }
}

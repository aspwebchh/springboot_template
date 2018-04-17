package com.dianhun.controller

import com.dianhun.common.Common
import com.dianhun.common.Contanst
import com.dianhun.common.ServerResult
import com.dianhun.config.AppConfig
import com.dianhun.repository.VideoRepository
import com.dianhun.service.CommonService
import com.dianhun.service.LoginService
import com.dianhun.service.VideoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.RequestMapping
import java.io.File
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse

@Controller
class Admin {
    @Autowired
    val videoService: VideoService? = null

    @Autowired
    val videoRepository : VideoRepository ? = null

    @Autowired
    val commonService: CommonService? = null

    @Autowired
    val response : HttpServletResponse?= null

    @Autowired
    val loginService : LoginService? = null

    @Autowired
    val appConfig : AppConfig? = null


    @RequestMapping("/check_login")
    fun checkLogin( pw: PrintWriter) {
        val success = loginService!!.isLogin()
        val result = if(success) {
            ServerResult(ServerResult.SUCCESS,"合法登录")
        } else {
            ServerResult(ServerResult.ERROR,"非法登录")
        }
        pw.println(Common.resultString(result))
    }

    @RequestMapping("/admin/video_list")
    fun videoList(map: ModelMap, page :Int?, begin_time: String?, end_time: String?, uid: String?) : String {
        if( loginService!!.isLogin() ) {
            val videoList = videoService!!.videoList(Contanst.PAGE_SIZE,page ?: 1, begin_time, end_time, uid)
            map["data_list"] = videoList.dataList
            map["data_count"] = videoList.dataCount
        }
        return "_video_list"
    }

    @RequestMapping("/del")
    fun del( id: Int, pw: PrintWriter ) {
        val result =  if(loginService!!.isLogin()) {
            val item = videoRepository!!.getOne(id)
            val file = File(item.filePath)
            if( file.exists()) {
                file.delete()
            }
            videoRepository!!.delete(id)
            ServerResult(ServerResult.SUCCESS,"删除成功")
        } else {
            ServerResult(ServerResult.ERROR,"未登录")
        }
        pw.println(Common.resultString(result))
    }
}

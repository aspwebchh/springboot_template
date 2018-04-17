package com.dianhun.service

import com.dianhun.common.ListAndCount
import com.dianhun.common.ServerResult
import com.dianhun.entity.VideoEntity
import com.dianhun.repository.VideoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.persistence.*
import javax.servlet.http.HttpServletRequest

@Service
class VideoService {
    @Autowired
    private val repository: VideoRepository? = null

    @PersistenceContext
    private val em: EntityManager? = null

    @Autowired
    private val request: HttpServletRequest? = null

    @Autowired
    private val commonService: CommonService? = null

    private fun todayDirName(): String {
        val df = SimpleDateFormat("yyyy_MM_dd")
        val date = Date()
        val dirName = df.format(date)
        return dirName
    }

    private fun getDir(): String {
        val basePath = System.getProperty("user.dir")
        val uploadDirPath = basePath + "\\upload"
        val dirPath = uploadDirPath + "\\" + todayDirName()
        val dir = File(dirPath)
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dirPath
    }

    private fun getEnableFileName(fileName: String, dirName: String): String {
        val filePath = dirName + "\\" + fileName
        val file = File(filePath)
        if (file.exists()) {
            val regex = Pattern.compile("(\\((?<num>\\d+)\\))?\\.(?=\\w+\$)")
            val matcher = regex.matcher(fileName)
            if (matcher.find()) {
                val num = matcher.group("num")
                val suffix = "(" + if (num != null) {
                    num.toInt() + 1
                } else {
                    1
                } + ")"
                val newName = matcher.replaceAll(suffix + ".")
                return getEnableFileName(newName, dirName)
            } else {
                return UUID.randomUUID().toString()
            }
        }
        return fileName
    }

    fun saveFile(video: MultipartFile): Pair<Boolean, String> {
        if (!video.isEmpty) {
            val dirName = getDir()
            val newFileName = getEnableFileName(video.originalFilename, dirName)
            val filePath = dirName + "\\" + newFileName
            val bytes = video.bytes
            val file = File(filePath)
            val buffStream = BufferedOutputStream(FileOutputStream(file))
            buffStream.write(bytes)
            buffStream.close()
            return Pair(true, filePath)
        } else {
            return Pair(false, "")
        }
    }

    private fun getDomain(): String {
        return commonService!!.getDomain()
    }

    private fun downloadUrl( id : Int ) : String  {
        return getDomain() + "/download?id=" + id
    }

    fun save(uid: String, video: MultipartFile): ServerResult {
        val (success, path) = saveFile(video)
        if (!success) {
            return ServerResult(ServerResult.FILE_INVALID, "文件上传失败", "")
        }

        val videoInfo = VideoEntity()
        videoInfo.filePath = path
        videoInfo.uid = uid
        videoInfo.addTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        videoInfo.fileSize = video.size.toInt()
        repository?.save(videoInfo)

        return ServerResult(ServerResult.SUCCESS, "上报成功", downloadUrl( videoInfo.id ) )
    }

    fun readFile(id: Int): ResponseEntity<FileSystemResource>? {
        repository?.let {
            val videoInfo = repository.getOne(id)
            val path = videoInfo.filePath
            val file = File(path)
            val fileSystemResource = FileSystemResource(file)
            val headers = HttpHeaders()
            headers.contentType = MediaType.parseMediaType("application/octet-stream")
            headers.setContentDispositionFormData("attachment", URLEncoder.encode( file.name ) )
            return ResponseEntity(fileSystemResource, headers, HttpStatus.OK)
        }
        return null
    }

    fun notify(url: String): ServerResult {
        val regex = Pattern.compile("id=(?<id>\\d+)")
        val matcher = regex.matcher(url)
        if (matcher.find()) {
            val id = matcher.group("id").toInt()
            try {
                val item = repository?.getOne(id)
                if (item != null) {
                    val file = File(item.filePath)
                    if( file.exists() ) {
                        file.delete()
                    }
                    repository?.delete(id)
                    return ServerResult(ServerResult.SUCCESS, "success")
                }
            }catch (ex:Exception) {
                return ServerResult(ServerResult.ERROR, ex.message.toString())
            }
        }
        return ServerResult(ServerResult.ERROR, "error")
    }

    fun videoList( pageSize: Int, pageIndex: Int, beginTime: String? , endTime: String?, uid: String? ) : ListAndCount<VideoEntity>{
        fun getLimit(): String = ((pageIndex - 1) * pageSize).toString() + "," + pageSize
        fun getWhere() :String {
            var where = " 1=1"
            if( beginTime != null) {
                where += " and add_time >= '$beginTime'"
            }
            if( endTime != null) {
                where += " and add_time <= '$endTime'"
            }
            if( uid != null ) {
                where += " and uid = '$uid'"
            }
            return where
        }
        val where = getWhere()
        val countSql = "select count(1) from video WHERE " + where
        val sql = "select * from video WHERE " + where  + " order by id desc limit " + getLimit()
        val countQuery = em!!.createNativeQuery(countSql)
        val query = em!!.createNativeQuery(sql, VideoEntity::class.java)
        val dataCount = (countQuery.singleResult as BigInteger).toInt()
        val dataList = query.resultList as List<VideoEntity>
        dataList.map {  item ->
            item.filePath = downloadUrl(item.id)
            item
        }
        return ListAndCount<VideoEntity>(dataList,dataCount)
    }
}

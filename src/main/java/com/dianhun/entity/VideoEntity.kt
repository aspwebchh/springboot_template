package com.dianhun.entity

import javax.persistence.*

@Entity
@Table(name = "video", schema = "dfdj_video", catalog = "")
class VideoEntity {
    @get:Id
    @get:Column(name = "id")
    @get:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0


    @get:Basic
    @get:Column(name = "file_path")
    var filePath: String? = null


    @get:Basic
    @get:Column(name = "add_time")
    var addTime: String? = null


    @get:Basic
    @get:Column(name = "uid")
    var uid: String? = null

    @get:Basic
    @get:Column(name = "file_size")
    var fileSize: Int = 0
}

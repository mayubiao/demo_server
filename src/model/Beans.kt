package com.bs.model

data class User(
    val id: Int?,
    val account:String?,
    val password:String?,
    val name:String?,
    val age:Int?,
    val logo:String?,
    val level:Int?,//登陆权限-> -1:游客,0:普通用户,1:管理级用户,2:管理员
    val status:Int?,
    val job:String?,
    val gender:String?,
    val phone:String?
)
data class AnnBean (
    var id:Int?,
    var title: String?,
    var content: String?,
    var date: String?

)

data class SjBean (
    var sjid:Int?,
    var title: String? ,
    var price: String?,
    var pic: String?
)
data class OrderBean(
    var oid:Int?,
    var userid:Int?,
    var sjid:Int?,
    var xt: String?,
    var neicun: String?,
    var xinghao: String?,
    var yanse: String?,
    var pingmuwaiguang: String?,
    var shexiangtou: String?,
    var jishenwaiguang: String?,
    var weixiushi: String?,
    var baojia:String?,
    var status:String?,//待报价,已报价,待验收,已完成
    var userInfoBean: User?,
    var sjBean:SjBean?
)


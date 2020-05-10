package com.bs

import common.Constants
import dao.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.features.ContentNegotiation
import io.ktor.gson.GsonConverter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import utils.DateUtil

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter())
    }
    //连接数据库
    Database.connect("jdbc:mysql://localhost:3306/data?characterEncoding=utf8&useSSL=false&serverTimezone=UTC"
        ,"com.mysql.cj.jdbc.Driver"
        ,"root"
        ,"root")
    transaction {
        SchemaUtils.create(UserTable)
        SchemaUtils.create(AnnTable)
        SchemaUtils.create(SjTable)
        SchemaUtils.create(OrderTable)
    }
    //路由
    routing {
        post("/login") {
            val params = call.receiveParameters()
            val acc = params["account"] ?: ""
            val psd = params["password"] ?: ""
            var re: Any? = null
            transaction {
                val query = UserTable.select { UserTable.account.eq(acc) }
                val pwd = query.joinToString { it[UserTable.password] }

                re = if (pwd.isEmpty() || pwd != psd) {
                    Constants.ERROR
                } else {
                    toUser(query.single())
                }

                commit()
            }
            call.respond(re!!)
        }
        post("/adminLogin") {
            val params = call.receiveParameters()
            val acc = params["account"] ?: ""
            val psd = params["password"] ?: ""
            var re: Any? = null
            transaction {
                val query = UserTable.select { UserTable.account.eq(acc) }
                val pwd = query.joinToString { it[UserTable.password] }
                val level=query.single()[UserTable.level]
                re = if (pwd.isEmpty() || pwd != psd||level!=2) {
                    Constants.ERROR
                } else {
                    Constants.SUCCESS
                }
                commit()
            }
            call.respond(re!!)
        }
        post("/getUserInfo") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"
            var re: Any? = null
            transaction {
                val query = UserTable.select { UserTable.id.eq(id.toInt()) }
                re = if (query.empty()){
                    Constants.ERROR
                }else{
                    toUser(query.single())
                }
                commit()
            }
            call.respond(re!!)
        }
        post("/resetPwd") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"
            val newpwd=params["newpwd"]?:""
            var re: Any? = null
            transaction {
                val query = UserTable.select { UserTable.id.eq(id.toInt()) }
                re = if (query.empty()){
                    Constants.ERROR
                }else{
                    UserTable.update({UserTable.id.eq(id.toInt())}) {
                        it[UserTable.password]=newpwd
                    }
                    Constants.SUCCESS
                }
                commit()
            }
            call.respond(re!!)
        }
        post("/getAllUser") {

            var re: Any? = null
            transaction {
                re= UserTable.selectAll().map { toUser(it) }.filter { it.level!=2 }
                commit()
            }
            call.respond(re!!)
        }
        post("/addAnn") {
            val params = call.receiveParameters()
            val title = params["title"] ?: ""
            val content = params["content"] ?: ""
            var re: Any? = null
            transaction {
                AnnTable.insert {
                    it[AnnTable.date]= DateUtil.getCurDateStr(DateUtil.FORMAT_YMDHMS)
                    it[AnnTable.content]=content
                    it[AnnTable.title]=title
                }
                re=Constants.SUCCESS
                commit()
            }
            call.respond(re!!)
        }
        post("/delAnn") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"

            var re: Any? = null
            transaction {
                AnnTable.deleteWhere { AnnTable.id.eq(id.toInt()) }
                re=Constants.SUCCESS
                commit()
            }
            call.respond(re!!)
        }
        post("/delUser") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"

            var re: Any? = null
            transaction {
                UserTable.deleteWhere { UserTable.id.eq(id.toInt()) }
                re=Constants.SUCCESS
                commit()
            }
            call.respond(re!!)
        }
        post("/resetUserInfo") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"
            val name=params["name"]?:""
            val phone=params["phone"]?:""
            val job=params["job"]
            val logo=params["logo"]?:""
            val gender=params["gender"]?:""
            val age=params["age"]?:"0"
            var re: Any? = null
            transaction {
                val query = UserTable.select { UserTable.id.eq(id.toInt()) }
                re = if (query.empty()){
                    Constants.ERROR
                }else{
                    UserTable.update({UserTable.id.eq(id.toInt())}) {
                        it[UserTable.name]=name
                        it[UserTable.phone]=phone
                        it[UserTable.age]=age.toInt()
                        it[UserTable.gender]=gender
                        it[UserTable.logo]=logo
                        it[UserTable.job]=job
                    }
                    Constants.SUCCESS
                }
                commit()
            }
            call.respond(re!!)
        }
        post("/register") {
            val params = call.receiveParameters()
            val acc = params["account"] ?: ""
            val pwd = params["password"] ?: ""
            val level=params["level"]?:"0"
            var re: Any? = null
            transaction {
                val query = UserTable.select { UserTable.account.eq(acc) }
                if (query.empty()){
                    UserTable.insert {
                        it[UserTable.account]=acc
                        it[UserTable.password]=pwd
                        it[UserTable.name]=acc
                        it[UserTable.level]=level.toInt()
                    }
                    re=Constants.SUCCESS
                }else{
                    re=Constants.ERROR
                }
                commit()
            }
            call.respond(re!!)
        }
        post("/getAnnList") {
            var re: Any? = null
            transaction {
                re= AnnTable.selectAll().map { toAnn(it) }.reversed()
                commit()
            }
            call.respond(re!!)
        }
        post("/getOrderList") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"
            var re: Any? = null
            transaction {
                if (id=="0"){
                    re=OrderTable.selectAll().map { toOrder(it) }.reversed()
                }else{
                    re= OrderTable.select { OrderTable.userid.eq(id.toInt()) }.map { toOrder(it) }.reversed()
                }

                commit()
            }
            call.respond(re!!)
        }
        post("/setBj") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"
            val price = params["price"] ?: ""
            var re: Any? = null
            transaction {
                OrderTable.update ({OrderTable.oid.eq(id.toInt())}){
                    it[OrderTable.status]="已报价"
                    it[OrderTable.baojia]=price

                }
                re=Constants.SUCCESS
                commit()
            }
            call.respond(re!!)
        }
        post("/setOrderStatus") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"
            val status = params["status"] ?: "0"
            var re: Any? = null
            transaction {
                OrderTable.update ({OrderTable.oid.eq(id.toInt())}){
                    it[OrderTable.status]=status
                }
                re=Constants.SUCCESS

                commit()
            }
            call.respond(re!!)
        }
        post("/delOrder") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"

            var re: Any? = null
            transaction {
                OrderTable.deleteWhere { OrderTable.oid.eq(id.toInt()) }
                re=Constants.SUCCESS

                commit()
            }
            call.respond(re!!)
        }
        post("/delsj") {
            val params = call.receiveParameters()
            val id = params["id"] ?: "0"

            var re: Any? = null
            transaction {
                SjTable.deleteWhere { SjTable.sjid.eq(id.toInt()) }
                re=Constants.SUCCESS

                commit()
            }
            call.respond(re!!)
        }
        post("/getSjList") {
            val params = call.receiveParameters()
            val key = params["key"] ?: ""
            var re: Any? = null
            transaction {
                re = if (key.isNullOrEmpty().not()){
                    SjTable.selectAll().map { toSj(it) }.filter { it.title!!.contains(key) }.reversed()
                }else{
                    SjTable.selectAll().map { toSj(it) }.reversed()
                }

                commit()
            }
            call.respond(re!!)
        }
        post("/addOrder") {
            val params = call.receiveParameters()
            val sjid = params["sjid"] ?: "0"
            val id = params["id"] ?: "0"
            val xt = params["xt"] ?: ""
            val neicun = params["neicun"] ?: ""
            val xinghao = params["xinghao"] ?: ""
            val yanse = params["yanse"] ?: ""
            val pingmuwaiguang = params["pingmuwaiguang"] ?: ""
            val shexiangtou = params["shexiangtou"] ?: ""
            val jishenwaiguang = params["jishenwaiguang"] ?: ""
            val weixiushi = params["weixiushi"] ?: ""
            var re: Any? = null
            transaction {
                OrderTable.insert {
                    it[OrderTable.jishenwaiguang]=jishenwaiguang
                    it[OrderTable.neicun]=neicun
                    it[OrderTable.yanse]=yanse
                    it[OrderTable.weixiushi]=weixiushi
                    it[OrderTable.pingmuwaiguang]=pingmuwaiguang
                    it[OrderTable.xinghao]=xinghao
                    it[OrderTable.xt]=xt
                    it[OrderTable.shexiangtou]=shexiangtou
                    it[OrderTable.sjid]=sjid.toInt()
                    it[OrderTable.userid]=id.toInt()
                }
                re=Constants.SUCCESS
                commit()
            }
            call.respond(re!!)
        }
        post("/addSj") {
            val params = call.receiveParameters()
            val title = params["title"] ?: ""
            val pic = params["pic"] ?: ""
            val price = params["price"] ?: ""
            var re: Any? = null
            transaction {
                SjTable.insert {
                    it[SjTable.pic]=pic
                    it[SjTable.title]=title
                    it[SjTable.price]=price
                }
                re=Constants.SUCCESS
                commit()
            }
            call.respond(re!!)
        }
    }
}


package dao

import com.bs.model.AnnBean
import com.bs.model.OrderBean
import com.bs.model.SjBean
import com.bs.model.User
import dao.SjTable.autoIncrement
import dao.SjTable.nullable
import dao.SjTable.primaryKey
import dao.UserTable.default
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.select

object UserTable : Table("user"){
    val id=integer("id").primaryKey(0).autoIncrement()
    val account= varchar("account", length = 255)
    val password= varchar("password", length = 255)
    val name= varchar("name", length = 255).nullable()
    val age= integer("age").default(0)
    val logo= varchar("logo", length = 255).nullable()
    val job= varchar("job", length = 255).nullable()
    val status= integer("status").default(0)
    val gender=varchar("gender",length = 255).nullable()
    val level=integer("level").default(0)
    val phone=varchar("phone", length = 255).nullable()
}
object AnnTable:Table("ann"){
    val id=integer("id").primaryKey(0).autoIncrement()
    val title= varchar("title", length = 255).nullable()
    val content= varchar("content", length = 255).nullable()
    val date= varchar("date", length = 255).nullable()
}
object SjTable:Table("sj"){
    val sjid=integer("sjid").primaryKey(0).autoIncrement()
    val title= varchar("title", length = 255).nullable()
    val price= varchar("price", length = 255).nullable()
    val pic= varchar("pic", length = 255).nullable()
}


object OrderTable:Table("order"){
    val oid= integer("oid").primaryKey(0).autoIncrement()
    val sjid= integer("sjid").default(0)
    val userid= integer("userid").default(0)
    val xt= varchar("xt", length = 255).nullable()
    val neicun= varchar("neicun", length = 255).nullable()
    val xinghao= varchar("xinghao", length = 255).nullable()
    val yanse= varchar("yanse", length = 255).nullable()
    val pingmuwaiguang= varchar("pingmuwaiguang", length = 255).nullable()
    val shexiangtou= varchar("shexiangtou", length = 255).nullable()
    val jishenwaiguang= varchar("jishenwaiguang", length = 255).nullable()
    val weixiushi= varchar("weixiushi", length = 255).nullable()
    val baojia= varchar("baojia", length = 255).nullable()
    val status= varchar("status", length = 255).default("待报价")
}
fun toSj(row: ResultRow):SjBean= SjBean(
    sjid = row[SjTable.sjid],
    title = row[SjTable.title],
    pic = row[SjTable.pic],
    price = row[SjTable.price]
)
fun toOrder(row: ResultRow):OrderBean= OrderBean(
    sjid = row[OrderTable.sjid],
    xt = row[OrderTable.xt],
    neicun = row[OrderTable.neicun],
    yanse = row[OrderTable.yanse],
    pingmuwaiguang = row[OrderTable.pingmuwaiguang],
    shexiangtou = row[OrderTable.shexiangtou],
    weixiushi = row[OrderTable.weixiushi],
    jishenwaiguang = row[OrderTable.jishenwaiguang],
    xinghao = row[OrderTable.xinghao],
    oid = row[OrderTable.oid],
    baojia=row[OrderTable.baojia],
    userid = row[OrderTable.userid],
    status = row[OrderTable.status],
    userInfoBean = toUser(UserTable.select { UserTable.id.eq( row[OrderTable.userid]) }.single()),
    sjBean = toSj(SjTable.select { SjTable.sjid.eq(row[OrderTable.sjid]) }.single())

)
fun toUser(row: ResultRow): User = User(
    account = row[UserTable.account],
    password = row[UserTable.password],
    age = row[UserTable.age],
    name = row[UserTable.name],
    logo =row[UserTable.logo],
    id=row[UserTable.id],
    gender=row[UserTable.gender],
    job =row[UserTable.job],
    status = row[UserTable.status],
    level=row[UserTable.level],
    phone=row[UserTable.phone]
)
fun toAnn(row: ResultRow):AnnBean= AnnBean(
    id = row[AnnTable.id],
    title = row[AnnTable.title],
    content = row[AnnTable.content],
    date = row[AnnTable.date]
)
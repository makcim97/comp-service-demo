package com.example

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.mongodb.MongoClient
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.routing.get
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import org.bson.Document
import org.slf4j.LoggerFactory
import java.io.File
import com.example.Utils.*
import org.apache.http.impl.client.CloseableHttpClient


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
var curUser: String = ""
@Suppress("unused")
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {

    }
    val server = embeddedServer(Netty, port = 80) {
        install(WebSockets)
        routing {
            webSocket("/") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site: $text")
                            if (text.toLowerCase() in arrayListOf("hello", "hi")) {
                                outgoing.send(Frame.Text("Hello. What can I do?"))
                            } else if (text.toLowerCase() in arrayListOf("добрый день", "здравствуйте", "привет")) {
                                outgoing.send(Frame.Text("Добрый день. Чем помочь?"))
                            } else if (text.toLowerCase() in arrayListOf("контакты", "где узнать контакты?", "связаться")) {
                                outgoing.send(Frame.Text("Список контактов вы можете посмотреть, перейдя в раздел Информация > Контакты"))
                            } else if (text.toLowerCase() in arrayListOf("оборудование", "где посмотреть список оборудования?", "где найти оборудования?")) {
                                outgoing.send(Frame.Text("Список имеющегося оборудования вы можете посмотреть в разделе Пользователь > Оборудование"))
                            } else if (text.toLowerCase() in arrayListOf("компания", "где узнать информацию о компании?", "информация о компании")) {
                                outgoing.send(Frame.Text("Информацию о компании вы можете посмотреть в разделе Информация > О компании"))
                            } else if (text.toLowerCase() in arrayListOf("как подать заявку?", "заявка на ремонт", "заявка")) {
                                outgoing.send(Frame.Text("Заявку на ремонт вы можете подать в разделе Пользователь > Подать заявку"))
                            } else if (text.toLowerCase() in arrayListOf("до свидания", "пока", "прощай")) {
                                outgoing.send(Frame.Text("До свидания. Если у вас возникнут вопросы, обращайтесь в любое время."))
                            } else if (text.toLowerCase() in arrayListOf("как закрыть чатбота?", "закрыть")) {
                                outgoing.send(Frame.Text("Чтобы закрыть чатбота перейдите на другой раздел сайта или на главную страницу."))
                            } else if (text != "StartBot") {
                                outgoing.send(Frame.Text("Не понимаю. I don't understand."))
                            }
                        }
                    }
                }
            }
            webSocket("Users") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site2: $text")
                            if (text == "GetUsers") {
                                val users = getUsers()
                                outgoing.send(Frame.Text(users))
                            }
                        }
                    }
                }
            }
            webSocket("Computers") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site2: $text")
                            if (text == "GetComputers") {
                                val computers = getComputers()
                                outgoing.send(Frame.Text(computers))
                            }
                        }
                    }
                }
            }
            webSocket("Notebooks") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site2: $text")
                            if (text == "GetNotebooks") {
                                val notebooks = getNotebooks()
                                outgoing.send(Frame.Text(notebooks))
                            }
                        }
                    }
                }
            }
            webSocket("Phones") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site2: $text")
                            if (text == "GetPhones") {
                                val phones = getPhones()
                                outgoing.send(Frame.Text(phones))
                            }
                        }
                    }
                }
            }
            webSocket("Requests") {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            println("From site2: $text")
                            if (text == "GetRequests") {
                                val phones = getRequests()
                                outgoing.send(Frame.Text(phones))
                            }
                        }
                    }
                }
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
            get("/") {
                call.respondFile(File("resources/Computers/Index.html"))
            }
            static {
                defaultResource("index.html", "Computers")
                resources("Computers")
            }
            post("/Login") {
                val receivedParams = call.receiveParameters()
                val login = receivedParams["login"]
                val status = receivedParams["status"]
                val pass = receivedParams["password"]
                val users = isUser(login!!)
                val pas = passForUser(login, pass!!)
                if (!users) {
                    call.respondFile(File("resources/Computers/errorLogin.html"))
                    if (login == "Admin" && pass == "AdmPSW.77A8Dc") {
                        call.respondFile(File("resources/Computers/admin.html"))
                        if (login == "User" && pass == "111") {
                            call.respondFile(File("resources/Computers/user.html"))
                        }
                    }
                } else if (!pas) {
                    call.respondFile(File("resources/Computers/errorPass.html"))
                    if (login == "Admin" && pass == "AdmPSW.77A8Dc") {
                        call.respondFile(File("resources/Computers/Admin.html"))
                        if (login == "User" && pass == "111") {
                            call.respondFile(File("resources/Computers/User.html"))
                        }
                    }
                }
                else if (login == "Admin" && pass == "AdmPSW.77A8Dc"){
                    curUser = "Admin"
                    call.respondFile(File("resources/Computers/AdminIndex.html"))
                    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as
                            LoggerContext
                    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
                    rootLogger.level = Level.OFF
                    val mongoUrl = "localhost"
                    val mongoClient = MongoClient(mongoUrl, 27017)
                    val mongoDatabase = mongoClient.getDatabase("Portal")
                    var userCollection = mongoDatabase.getCollection("User")
                    val usersCount = userCollection.countDocuments() //кол-во документов
                    println("usersCount = $usersCount")
                    var users = ArrayList<Document>()
                    val iter = userCollection.find()
                    users.clear()
                    iter.into(users)
                    println("users = $users") //вывод в консоль

                    var computersCollection = mongoDatabase.getCollection("Computers")
                    val computerCount = computersCollection.countDocuments()
                    println("ComputerCount = $computerCount")
                    var computers = ArrayList<Document>()
                    val iter1 = computersCollection.find()
                    computers.clear()
                    iter1.into(computers)
                    println("computers = $computers")
                    var phonesCollection = mongoDatabase.getCollection("Phones")
                    val phoneCount = phonesCollection.countDocuments()
                    println("PhoneCount = $phoneCount")
                    var phones = ArrayList<Document>()
                    val iter3 = phonesCollection.find()
                    phones.clear()
                    iter3.into(phones)
                    println("phones = $phones")

                    var notebooksCollection = mongoDatabase.getCollection("Notebooks")
                    val notebooksCount = notebooksCollection.countDocuments()
                    println("notebooksCount = $notebooksCount")
                    var notebooks = ArrayList<Document>()
                    val iter2 = notebooksCollection.find()
                    notebooks.clear()
                    iter2.into(notebooks)
                    println("notebooks = $notebooks")

                    var requestsCollection = mongoDatabase.getCollection("User_Request")
                    val requestsCount = requestsCollection.countDocuments()
                    println("requestsCount = $requestsCount")
                    var requests = ArrayList<Document>()
                    val iter4 = requestsCollection.find()
                    requests.clear()
                    iter4.into(requests)
                    println("notebooks = $requests")
                }
                else if(users && pas){
                    curUser = "User"
                    call.respondFile(File("resources/Computers/UserIndex.html"))
                }
            }
            get("/Admin.html") {
                if (curUser=="Admin") call.respondFile(File("resources/Computers/Admin.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/AdminContact.html") {
                if (curUser=="Admin") call.respondFile(File("resources/Computers/AdminContact.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/AdminIndex.html") {
                if (curUser=="Admin") call.respondFile(File("resources/Computers/AdminIndex.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/Userlist.html") {
                if (curUser=="Admin") call.respondFile(File("resources/Computers/Userlist.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("Logout"){
                val receivedParams = call.request.queryParameters["login"]
                println("logout from ${receivedParams}")
                curUser = ""
                call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/User.html") {
                if (curUser=="User") call.respondFile(File("resources/Computers/User.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/socketBot.html") {
                if (curUser=="User") call.respondFile(File("resources/Computers/socketBot.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/UserContact.html") {
                if (curUser=="User") call.respondFile(File("resources/Computers/UserContact.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/Computerlist.html") {
                if (curUser=="User") call.respondFile(File("resources/Computers/Computerlist.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/UserIndex.html") {
                if (curUser=="User") call.respondFile(File("resources/Computers/UserIndex.html"))
                else call.respondFile(File("resources/Computers/Index.html"))
            }
            get("/demo") {
                call.respondFile(File("resources/Computers/socketBot.html"))
            }
            post("AddRequest"){
                val receivedParams = call.receiveParameters()
                val fio = receivedParams["FIO"]
                val type = receivedParams["type"]
                val model = receivedParams["model"]
                val broken = receivedParams["broken"]
                val phone = receivedParams["phone"]
                val comment = receivedParams["comment"]
                val newReq = Request(fio!!, type!!, model!!, broken!!, phone!!, comment!!)
                println("newReq = $newReq")
                addRequest(newReq)
                call.respondRedirect("User.html", permanent = true)
            }
            post("AddDocument"){
                val receivedParams = call.receiveParameters()
                val fio = receivedParams["FIO"]
                val type = receivedParams["type"]
                val model = receivedParams["model"]
                val data = receivedParams["data"]
                val price = receivedParams["price"]
                val newReq = Documents(fio!!, type!!, model!!, data!!, price!!)
                println("newReq = $newReq")
                addDocument(newReq)
                call.respondRedirect("Admin.html", permanent = true)
            }
            post("AddUser"){
                val receivedParams = call.receiveParameters()
                val fio = receivedParams["FIO"]
                val status = receivedParams["status"]
                val login = receivedParams["login"]
                val pass = receivedParams["password"]
                val newAcc = Account(login!!, fio!!, status!!, pass!!)
                println("newAcc = $newAcc")
                addUser(newAcc)
                call.respondRedirect("Userlist.html", permanent = true)
            }
            post("AddComputer"){
                val receivedParams = call.receiveParameters()
                val model = receivedParams["model"]
                val OS = receivedParams["OS"]
                val processor = receivedParams["processor"]
                val price = receivedParams["price"]
                val newComp = Computer(model!!, OS!!, processor!!, price!!)
                println("newComp = $newComp")
                addComputer(newComp)
                call.respondRedirect("AdminComputerlist.html", permanent = true)
            }
            post("AddPhone"){
                val receivedParams = call.receiveParameters()
                val model = receivedParams["model"]
                val company = receivedParams["company"]
                val display = receivedParams["display"]
                val price = receivedParams["price"]
                val newPhone = Phone(model!!, company!!, display!!, price!!)
                println("newPhone = $newPhone")
                addPhone(newPhone)
                call.respondRedirect("AdminComputerlist.html", permanent = true)
            }
            post("AddNotebook"){
                val receivedParams = call.receiveParameters()
                val model = receivedParams["model"]
                val OS = receivedParams["OS"]
                val processor = receivedParams["processor"]
                val price = receivedParams["price"]
                val newNote = Notebook(model!!, OS!!, processor!!, price!!)
                println("newNote = $newNote")
                addNotebook(newNote)
                call.respondRedirect("AdminComputerlist.html", permanent = true)
            }
            post ("ToMSOffice") {
                val receivedParams = call.receiveParameters()
                println("To office = ")
                when (receivedParams["toOffice"]){
                    "Excel" -> {
                        println("Excel")
                        val file = toExcel(arrayOf("ФИО", "Логин", "Статус"), getUsersList())
                        if(file!!.exists()) {
                            call.respondFile(file)
                        } else call.respond(HttpStatusCode.NotFound)
                    }
                    "Word" -> {
                        println("Word")
                        val file = toWordDocx(arrayOf("ФИО", "Логин", "Статус"), getUsersList())
                        if(file!!.exists()) {
                            call.respondFile(file)
                        } else call.respond(HttpStatusCode.NotFound)
                    }
                }
            }
        }
    }
    server.start(wait = true) //Старт сервера
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

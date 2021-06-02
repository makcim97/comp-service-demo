package com.example.Utils

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import com.mongodb.MongoClient
import com.mongodb.util.JSON.serialize
import io.ktor.utils.io.errors.IOException
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.*
import org.bson.Document
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.MessageDigest

data class Account(var login: String, var fio: String, var status: String, var pass: String)
data class Computer(var model: String, var OS: String, var processor: String, var price: String)
data class Notebook(var model: String, var OS: String, var processor: String, var price: String)
data class Phone(var model: String, var company: String, var display: String, var price: String)
data class Request(var FIO: String, var type: String, var model: String, var broken: String, var phone: String, var comment: String)
data class Documents(var FIO: String, var type: String, var model: String, var data: String, var price: String)

fun addRequest(req: Request){
    var newReq = Document("_id", ObjectId())
    newReq.append("FIO",req.FIO)
        .append("type",req.type)
        .append("model", req.model)
        .append("broken",req.broken)
        .append("phone",req.phone)
        .append("comment",req.comment)
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var compCollection = mongoDatabase.getCollection("User_Request")
    compCollection.insertOne(newReq)
}

fun addDocument(doc: Documents){
    var newDoc = Document("_id", ObjectId())
    newDoc.append("FIO",doc.FIO)
        .append("type",doc.type)
        .append("model", doc.model)
        .append("data",doc.data)
        .append("price",doc.price)
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var compCollection = mongoDatabase.getCollection("Documents")
    compCollection.insertOne(newDoc)
}

fun addComputer(comp: Computer){
    var newComp = Document("_id", ObjectId())
    newComp.append("model",comp.model)
        .append("OS",comp.OS)
        .append("processor", comp.processor)
        .append("price",comp.price)
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var compCollection = mongoDatabase.getCollection("Computers")
    compCollection.insertOne(newComp)
}

fun addPhone(comp: Phone){
    var newPhone = Document("_id", ObjectId()) //создаем документ для БД
    newPhone.append("model",comp.model) //вставляем в него поля с нужными данными из объекта user
        .append("company",comp.company)
        .append("display", comp.display)
        .append("price",comp.price)
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF //выключаем лишние сообщения от MongoDB
    val mongoUrl = "localhost" //адрес локального сервера MongoDB
    val mongoClient = MongoClient(mongoUrl, 27017) //клиент MongoDB
    val mongoDatabase = mongoClient.getDatabase("Portal") //выбираем БД
    var phoneCollection = mongoDatabase.getCollection("Phones") //выбираем коллекцию
    phoneCollection.insertOne(newPhone) //вставляем новый документ в коллекцию
}

fun addNotebook(note: Notebook){
    var newNotebook = Document("_id", ObjectId()) //создаем документ для БД
    newNotebook.append("model",note.model) //вставляем в него поля с нужными данными из объекта user
        .append("OS",note.OS)
        .append("processor", note.processor)
        .append("price",note.price)
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF //выключаем лишние сообщения от MongoDB
    val mongoUrl = "localhost" //адрес локального сервера MongoDB
    val mongoClient = MongoClient(mongoUrl, 27017) //клиент MongoDB
    val mongoDatabase = mongoClient.getDatabase("Portal") //выбираем БД
    var noteCollection = mongoDatabase.getCollection("Notebooks") //выбираем коллекцию
    noteCollection.insertOne(newNotebook) //вставляем новый документ в коллекцию
}

fun addUser(user: Account){
    var newUser = Document("_id", ObjectId())
    newUser.append("login",user.login)
        .append("fio",user.fio)
        .append("status", user.status)
        .append("pass",user.pass.sha1())
    val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var userCollection = mongoDatabase.getCollection("User")
    userCollection.insertOne(newUser)
}

fun getUsers(): String {
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var userCollection = mongoDatabase.getCollection("User")
    println("From Mongo = $userCollection")
    val usersCount = userCollection.countDocuments()
    println("usersCount = $usersCount")
    var users = ArrayList<Document>()
    val iter = userCollection.find()
    users.clear()
    iter.into(users)
    val usersString = usersToString(users)
    return usersString
}

fun getNotebooks(): String {
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var notebookCollection = mongoDatabase.getCollection("Notebooks")
    println("From Mongo = $notebookCollection")
    val notebooksCount = notebookCollection.countDocuments()
    println("notebookCount = $notebooksCount")
    var notebooks = ArrayList<Document>()
    val iter1 = notebookCollection.find()
    notebooks.clear()
    iter1.into(notebooks)
    val notebooksString = notebooksToString(notebooks) // функция описана ниже
    return notebooksString
}

fun getPhones(): String {
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var phoneCollection = mongoDatabase.getCollection("Phones")
    println("From Mongo = $phoneCollection")
    val phoneCount = phoneCollection.countDocuments()
    println("PhoneCount = $phoneCount")
    var phones = ArrayList<Document>()
    val iter1 = phoneCollection.find()
    phones.clear()
    iter1.into(phones)
    val usersString = phonesToString(phones) // функция описана ниже
    return usersString
}

fun getComputers(): String {
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var computerCollection = mongoDatabase.getCollection("Computers")
    println("From Mongo = $computerCollection")
    val computersCount = computerCollection.countDocuments()
    println("CompCount = $computersCount")
    var computers = ArrayList<Document>()
    val iter1 = computerCollection.find()
    computers.clear()
    iter1.into(computers)
    val usersString = usersToString(computers) // функция описана ниже
    return usersString
}

fun getRequests(): String {
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var computerCollection = mongoDatabase.getCollection("User_Request")
    println("From Mongo = $computerCollection")
    val computersCount = computerCollection.countDocuments()
    println("CompCount = $computersCount")
    var computers = ArrayList<Document>()
    val iter1 = computerCollection.find()
    computers.clear()
    iter1.into(computers)
    val usersString = requestsToString(computers) // функция описана ниже
    return usersString
}

fun usersToString(users: ArrayList<Document>): String{
    var usersString = """[""" //начало итоговой строки
    users.forEach { // для всех пользователей
        val json = serialize(it) //преобразуем объект в JSON-строку и добавляем к итоговой строке
        usersString = usersString + json + ""","""
    }
    usersString=usersString.dropLast(1) //удаляем последний символ - запятую, вместо нее вставляем
    usersString = usersString + """]""" // квадратную скобку
    println("usersString = $usersString ")
    return usersString
}

fun notebooksToString(notebooks: ArrayList<Document>): String{
    var notebooksString = """[""" //начало итоговой строки
    notebooks.forEach { // для всех пользователей
        val json = serialize(it) //преобразуем объект в JSON-строку и добавляем к итоговой строке
        notebooksString = notebooksString + json + ""","""
    }
    notebooksString=notebooksString.dropLast(1) //удаляем последний символ - запятую, вместо нее вставляем
    notebooksString = notebooksString + """]""" // квадратную скобку
    println("notebooksString = $notebooksString ")
    return notebooksString
}

fun phonesToString(phones: ArrayList<Document>): String{
    var phonesString = """[""" //начало итоговой строки
    phones.forEach { // для всех пользователей
        val json = serialize(it) //преобразуем объект в JSON-строку и добавляем к итоговой строке
        phonesString = phonesString + json + ""","""
    }
    phonesString=phonesString.dropLast(1) //удаляем последний символ - запятую, вместо нее вставляем
    phonesString = phonesString + """]""" // квадратную скобку
    println("phonesString = $phonesString ")
    return phonesString
}

fun computersToString(computers: ArrayList<Document>): String{
    var computersString = """[""" //начало итоговой строки
    computers.forEach { // для всех пользователей
        val json = serialize(it) //преобразуем объект в JSON-строку и добавляем к итоговой строке
        computersString = computersString + json + ""","""
    }
    computersString=computersString.dropLast(1) //удаляем последний символ - запятую, вместо нее вставляем
    computersString = computersString + """]""" // квадратную скобку
    println("computersString = $computersString ")
    return computersString
}

fun requestsToString(computers: ArrayList<Document>): String{
    var computersString = """[""" //начало итоговой строки
    computers.forEach { // для всех пользователей
        val json = serialize(it) //преобразуем объект в JSON-строку и добавляем к итоговой строке
        computersString = computersString + json + ""","""
    }
    computersString=computersString.dropLast(1) //удаляем последний символ - запятую, вместо нее вставляем
    computersString = computersString + """]""" // квадратную скобку
    println("requestsString = $computersString ")
    return computersString
}

fun isUser(user: String): Boolean {
    var userValid = false
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    val userCollection = mongoDatabase.getCollection("User")
    val docs: ArrayList<Document> = ArrayList<Document>()
    val iter = userCollection.find()
    docs.clear()
    iter.into(docs)
    val findLogin = docs.filter { it["login"] == user }
    if (findLogin.isNotEmpty()) userValid = true
    return userValid
}
fun passForUser(user: String, pass: String): Boolean {
    var passValid = false
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    val userCollection = mongoDatabase.getCollection("User")
    val docs = ArrayList<Document>()
    val iter = userCollection.find()
    docs.clear()
    iter.into(docs)
    val findLogin = docs.filter { it["login"] == user }
    if (findLogin.isNotEmpty()) {
        println("!Match! Login = ${findLogin.first()["login"]}")
        val user = findLogin.first()
        if (user["pass"] == pass.sha1()) {
            passValid = true
        }
    }
    return passValid
}

fun String.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32, '0')
}

fun String.sha1(): String {
    val md = MessageDigest.getInstance("SHA-1")
    return BigInteger(1, md.digest(toByteArray())).toString(16).padStart(32,'0')
}

fun main(){
    print("Input string: ")
    val str = readLine()
    val strMD5 = str?.md5()
    println("md5 = $strMD5")
    val strSHA1 = str?.sha1()
    println("sha1 = $strSHA1")
}

var book : Workbook? = null //книга в Excel
//метод для записи данных в файл Excel в формате xlsx
//первый параметр - строковый массив с заголовками колонок, второй параметр - список с данными
fun toExcel(tableTitles: Array<String>, accList: List<Document>): File? {
    book = XSSFWorkbook() //создаем книгу
    val sheet = book!!.createSheet("AccountsInfo") //создаем лист
    sheet.addMergedRegion( //создадим объединение из 4-х ячеек, чтоб сделать шапку таблицы
        CellRangeAddress( //добавляем в книгу объединенный диапазон ячеек
            0, //начальный ряд диапазона
            0, //конечный ряд диапазона
            0, //начальная колонка диапазона
            3 //конечная колонка диапазона
        )
    )
    val row: Row = sheet.createRow(0) //создаем новый ряд
    val cell: Cell = row.createCell(0) //создаем ячейку
    cell.setCellValue("!! Информация об аккаунтах !!") //задаем текст ячейки
    val cellStyle = book!!.createCellStyle() as XSSFCellStyle // стиль для ряда
    cellStyle.alignment = CellStyle.ALIGN_CENTER //задаем выравнивание по центру
    val font: Font = book!!.createFont() //создаем шрифт для объединенных ячеек
    font.setFontHeightInPoints(14.toShort()) //задаем размер шрифта
    font.setColor(HSSFColor.RED.index) //задаем цвет шрифта
    cellStyle.setFont(font) //добавляем шрифт к стилю
    cell.cellStyle = cellStyle //устанавливаем стиль на ячейку
    headCreate(sheet, tableTitles) //вызываем метод для заголовка таблицы (описан ниже)
    dataToSheet(sheet, accList) //вызываем метод для заполнения данных таблицы (описан ниже)
    for (i in tableTitles.indices) { //цикл для установки ширины ячеек по содержимому
        sheet.autoSizeColumn(i)
    }
    val file = File("myExcel.xlsx") //файл для записи данных
    try { // Записываем всё в файл
        book!!.write(FileOutputStream(file)) //пишем книгу в файл
        book!!.close() //закрываем книгу
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file // возвращаем созданный файл
}
//метод для создания заголовка таблицы
private fun headCreate(sheet: Sheet, tableTitles: Array<String>) {
    // Нумерация рядов начинается с нуля
    val row: Row = sheet.createRow(1) //создаем новый ряд
    val cellStyle = book!!.createCellStyle() as XSSFCellStyle //создаем стиль для ряда
    cellStyle.alignment = CellStyle.ALIGN_CENTER //задаем выравнивание по центру
    cellStyle.borderBottom = CellStyle.BORDER_THICK //задаем нижнюю границу
    cellStyle.borderLeft = CellStyle.BORDER_THICK //и все остальные
    cellStyle.borderRight = CellStyle.BORDER_THICK
    cellStyle.borderTop = CellStyle.BORDER_THICK
    val font: Font = book!!.createFont() //создаем объект для параметров шрифта
    font.setBold(true) //делаем шрифт жирным
    cellStyle.setFont(font) //устанавливаем шрифт в стиль
    for (i in tableTitles.indices) { //цикл по заголовкам
        val temp: Cell = row.createCell(i) //создаем ячейку
        temp.cellStyle = cellStyle //задаем ей стиль
        temp.setCellValue(tableTitles[i]) //задаем ей значение
    }
}
//метод для заполнения данных таблицы
private fun dataToSheet(sheet: Sheet, accList: List<Document>): Int {
    var i = 1 //счетчик кол-ва рядов
    val cellStyle = book!!.createCellStyle() as XSSFCellStyle //создаем стиль для ряда
    cellStyle.borderBottom = CellStyle.BORDER_THIN //задаем нижнюю границу
    cellStyle.borderLeft = CellStyle.BORDER_THIN //и все остальные
    cellStyle.borderRight = CellStyle.BORDER_THIN
    cellStyle.borderTop = CellStyle.BORDER_THIN
    for (accInfo in accList) { //цикл по списку полей
        i++ //счетчик добавленных строк - рядов
        val row: Row = sheet.createRow(i) //создаем новый ряд
        var temp: Cell = row.createCell(0) //задаем первую ячейку
        temp.setCellValue(accInfo["fio"].toString()) //вставляем туда ФИО
        temp.cellStyle = cellStyle //устанавливаем стиль созданной ячейки
        temp = row.createCell(1) //задаем вторую ячейку
        temp.setCellValue(accInfo["login"].toString()) //вставляем туда логин
        temp.cellStyle = cellStyle
        temp = row.createCell(2) //задаем третью ячейку
        temp.setCellValue(accInfo["status"].toString()) //вставляем туда статус пользователя
        temp.cellStyle = cellStyle
    }
    return i //можно потом использовать, чтобы узнать реальное кол-во вставленных строк
}
//метод для записи данных в docx файл, параметры - массив с заголовками для столбцов
//и список List с содержимым таблицы
fun toWordDocx(tableTitles: Array<String?>, accList: List<Document>): File? {
    val document = XWPFDocument() //создаем документ Word
    val paragraph: XWPFParagraph = document.createParagraph() //создаем абзац в документе
    val run: XWPFRun = paragraph.createRun() //создаем объект для записи в полученный ранее абзац
    run.setText("Информация об аккаунтах") //текст перед таблицей
    val table: XWPFTable = document.createTable() //создаем таблицу
    val tableHead: XWPFTableRow = table.getRow(0) //создаем первый ряд - заголовок таблицы
    for (i in tableTitles.indices) { //цикл по заголовкам
        var paragraph1: XWPFParagraph? = null //объявляем объект для параграфа
        paragraph1 = if (i == 0) { //если первая ячейка, то сразу берем у нее параграф
            tableHead.getCell(0).getParagraphs().get(0)
        } else { //иначе создаем новую ячейку и берем у нее параграф
            val cell: XWPFTableCell = tableHead.addNewTableCell()
            cell.getParagraphs().get(0)
        }
        paragraph1.setAlignment(ParagraphAlignment.CENTER) //устанавливаем выравнивание по центру
        val tableHeadRun: XWPFRun = paragraph1.createRun() //создаем объект для записи в абзац
        tableHeadRun.setBold(true) //устанавливаем жирный шрифт
        tableHeadRun.setText(tableTitles[i]) //задаем текст
    }
    for (accInfo in accList) { //цикл по списку аккаунтов
        val tableRow: XWPFTableRow = table.createRow() //создаем ряд
        tableRow.getCell(0).setText(accInfo["fio"].toString()) //заполняем ячейки ряда данными
        tableRow.getCell(1).setText(accInfo["login"].toString())
        tableRow.getCell(2).setText(accInfo["status"].toString())
    }
    val file = File("createparagraph.docx") //создаем файл
    try { //создаем файловый поток вывода с новым файлом
        val out = FileOutputStream(file)
        document.write(out) //пишем в файл из созданного объекта
        out.close() // закрываем потоки вывода
        document.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    println("createparagraph.docx written successfully")
    return file //возвращаем файл
}
fun getUsersList(): ArrayList<Document> { //функция получения списка аккаунтов из MongoDB
    val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
    val rootLogger = loggerContext.getLogger("org.mongodb.driver")
    rootLogger.level = Level.OFF
    val mongoUrl = "localhost"
    val mongoClient = MongoClient(mongoUrl, 27017)
    val mongoDatabase = mongoClient.getDatabase("Portal")
    var userCollection = mongoDatabase.getCollection("User")
    println("From Mongo = $userCollection")
    val usersCount = userCollection.countDocuments()
    println("usersCount = $usersCount")
    var users = ArrayList<Document>()
    val iter = userCollection.find()
    users.clear()
    iter.into(users)
    return users
}

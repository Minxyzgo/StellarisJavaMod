import java.io.*
import kotlin.concurrent.thread
import kotlin.jvm.Throws


try {
    val mdt = "Mindustry"
    lateinit var jarFile: File
    val projectname = System.getProperty("user.dir").run { File(this).parent }
    val pn = projectname.substring(projectname.lastIndexOf('\\') + 1, projectname.length)
    Runtime.getRuntime().exec("taskkill /IM $mdt.exe")

    thread(start = true) {
        jarFile = File("$projectname\\build\\libs\\${pn}Desktop.jar".apply { println(this) })
        if (jarFile.exists().apply {
                    println(this)
                }) copy(jarFile, File(System.getenv("${mdt.toUpperCase()}_MOD_HOME")))

        Runtime.getRuntime().exec("${System.getenv("${mdt.toUpperCase()}_HOME")}\\Mindustry.exe")
    }

} catch (e: Exception) {
    e.printStackTrace()
}

//@Throws(Exception::class)
//fun killProcess(processName: String): Boolean {
//    val cmd =
//    val process =
//
//    val reader = BufferedReader(InputStreamReader(
//            process.inputStream))
//    var line: String? = null
//    println(reader.readLine())
//    while (reader.readLine().also { line = it } != null) {
//
//        if (line!!.indexOf(processName) != -1) {
//            val p = Runtime.getRuntime().exec("taskkill /F /IM $processName")
//            val br = BufferedReader(InputStreamReader(p.inputStream, "gbk"))
//            br.readLine().toString()
//            println("kill finish")
//            return true
//        } else return false
//    }
//
//    return false
//}

@Throws(Exception::class)
fun copy(file: File, toFile: File) {
    val b = ByteArray(1024)
    var a: Int
    val fis: FileInputStream
    val fos: FileOutputStream
    var filepath = file.absolutePath
    var toFilepath = toFile.absolutePath
    if (file.isDirectory) {
        filepath = filepath.replace("\\\\".toRegex(), "/")
        toFilepath = toFilepath.replace("\\\\".toRegex(), "/")
        val lastIndexOf = filepath.lastIndexOf("/")
        toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length)
        val copy = File(toFilepath)
        if (!copy.exists()) {
            copy.mkdir()
        }
        for (f in file.listFiles()) {
            copy(f!!, copy)
        }
    } else {
        if (toFile.isDirectory) {
            filepath = filepath.replace("\\\\".toRegex(), "/")
            toFilepath = toFilepath.replace("\\\\".toRegex(), "/")
            val lastIndexOf = filepath.lastIndexOf("/")
            toFilepath = toFilepath + filepath.substring(lastIndexOf, filepath.length)
            val newFile = File(toFilepath)
            fis = FileInputStream(file)
            fos = FileOutputStream(newFile)
            while (fis.read(b).also { a = it } != -1) {
                fos.write(b)
            }
        } else {
            fis = FileInputStream(file)
            fos = FileOutputStream(toFile)
            while (fis.read(b).also { a = it } != -1) {
                fos.write(b)
            }
        }

        fis.close()
        fos.close()
    }
}
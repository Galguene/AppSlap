import java.lang.ProcessBuilder.*
import java.io.File
import java.io.InputStream.*
import java.io.IOException
import kotlin.system.exitProcess

fun cliApp(cmd: String) {
    var args = listOf("/bin/bash","-c",cmd)
    var procBuilder = ProcessBuilder().command(args)
    var process = procBuilder.start()
    val output = process.getInputStream().bufferedReader().readLines()
    for ((_,v) in output.withIndex()) {
        println(v)
    }
}

fun guiApp(app: String) {
    var procBuilder = ProcessBuilder().command(app)
    procBuilder.start()
}

fun execApps(cat: String) {
    val path = System.getProperty("user.dir")
    val apps = File("${path}/applist.csv").bufferedReader().readLines()
    var appProps: List<String>
    for ((_,v) in apps.withIndex()) {
        appProps = v.split(",")
        if (appProps[0] == cat) {
            if (appProps[1] == "gui") {
                guiApp(appProps[2])
            } else {
                cliApp(appProps[2])
            }
        }
    }
}

fun listApps() {
    val path = System.getProperty("user.dir")
    val apps = File("${path}/applist.csv").bufferedReader().readLines()
    for ((i,v) in apps.withIndex()) {
        println("$i - $v")
    }
}

fun orderApps() {
    val path = System.getProperty("user.dir")
    var apps = File("${path}/applist.csv").bufferedReader().readLines()
    for ((i,v) in apps.withIndex()) {
        println("$i - $v")
    }
    println("First line to swap:")
    val lineOne = readLine()!!
    println("Second line to swap with:")
    val lineTwo = readLine()!!
    var appArr = apps.toTypedArray()
    var temp = appArr[lineOne.toInt()]
    appArr[lineOne.toInt()] = appArr[lineTwo.toInt()]
    appArr[lineTwo.toInt()] = temp
    File("${path}/applist.csv").bufferedWriter().use { out ->
        for ((_,v) in appArr.withIndex()) {
            out.write(v)
            out.newLine()
        }
    }
}

fun addApp(cat: String, typ: String, app: String, ) {
    val path = System.getProperty("user.dir")
    File("${path}/applist.csv").appendText("$cat,$typ,$app")
}

fun main(args: Array<String?>) {
    if (args.size > 0 && args[0]!![0].toString() == "-") {
        when (args[0]) {
            "-help" -> {
                println("AppSlap - CLI tool to open multiple apps at once, both graphical and command-line")
                println("Usage: ata [tool][argument][category]")
                println("Tools:")
                println("-list - List current apps in all categories")
                println("-order - Reorder apps by number")
                println("-addgui - Add a GUI app to the list. If no category, goes in Default")
                println("-addcli - Add a CLI app to the list. If no category, goes in Default")
                println("Add arguments can be used multiple times in a row.")
                }
            "-order" -> {
                orderApps()
            }
            "-list" -> {
                listApps()
            }
            "-addgui" -> {
                println("Opening app to check if it works...")
                var app = args.slice(1..(args.size-1)).joinToString(" ")
                try {
                    guiApp(app)
                } catch (e: ArrayIndexOutOfBoundsException){
                    println("Argument error, no app passed")
                    exitProcess(1)
                } catch (e: IOException) {
                    println("No app named ${args[1]}, or wrong arguments passed")
                    exitProcess(2)
                } finally {
                    println("Success!")
                    println("Category for app:")
                    val cat = readLine()!!
                    addApp(cat,"gui",app)
                }
            }
            "-addcli" -> {
                println("Opening app to check if it works...")
                var cmd = args.slice(1..(args.size-1)).joinToString(" ")
                try {
                    cliApp(cmd)
                } catch (e: ArrayIndexOutOfBoundsException){
                    println("Argument error, no command passed")
                    exitProcess(1)
                } catch (e: IOException) {
                    println("No command named ${args[1]}, or wrong arguments passed")
                    exitProcess(2)
                } finally {
                    println("Success!")
                    println("Category for app:")
                    val cat = readLine()!!
                    addApp(cat,"cli",cmd)
                }
            } else -> {
                println("Wrong argument:${args[0]}")
            }
        }
        exitProcess(0)
    }

    execApps("Default")
    if (args.size > 0) {
        for ((_,cat) in args.withIndex()) {
            execApps(cat!!)
        }
    }
    exitProcess(0)
}
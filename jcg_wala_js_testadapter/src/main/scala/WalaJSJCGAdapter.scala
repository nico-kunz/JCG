import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors
import scala.collection.mutable
import scala.jdk.CollectionConverters._
import upickle.default._


object WalaJSJCGAdapter extends JSTestAdapter {

    val possibleAlgorithms: Array[String] = Array("1-CFA", "0-1-CFA")

    val frameworkName: String = "WALA-JS"

    def main(args: Array[String]): Unit = serializeAllCGs("testcasesOutput/js", s"results/js/$frameworkName")

    private def serializeAllCGs(inputDirPath: String, outputDirPath: String): Unit = {
        for (algo <- possibleAlgorithms) {
            generateCallGraphs(inputDirPath, outputDirPath, algo)
        }
    }

    private def generateCallGraphs(inputDirPath: String, outputDirPath: String, algorithm: String): Unit = {
        val outputDir = new File(s"$outputDirPath/$algorithm")
        outputDir.mkdirs()
        val testDirs = new File(inputDirPath).list().filter(x => new File(s"$inputDirPath/$x").isDirectory)
        println("test_dirs:" ,testDirs.mkString(", "))

        // generate callgraph for every testcase
        testDirs.foreach(testDir => {
            val output = new FileWriter(outputDir.getAbsolutePath + "/" + testDir + ".json")

            println(new File(inputDirPath).getAbsolutePath)
            val fileToParse = new File(s"$inputDirPath/$testDir").listFiles().last.getAbsolutePath
            val file_arr = Array(fileToParse, outputDir.getAbsolutePath)
            println(testDir)
            println("THE FILE TO PROCESS IS: " + file_arr.head)
            serializeCG(algorithm, fileToParse, output, AdapterOptions.makeEmptyOptions())
            output.close()
        })
        println("Call graphs generated!")
    }

    def serializeCG(
                       algorithm:      String,
                       inputDirPath:   String,
                       output:         Writer,
                       adapterOptions: AdapterOptions
                   ): Long = {
        val tempFile = new File(s"temp/$frameworkName/$algorithm/out.dot")
        tempFile.getParentFile.mkdirs()
        val start = System.currentTimeMillis()
        WalaConverter.main(Array(inputDirPath, tempFile.getAbsolutePath))
        val end = System.currentTimeMillis()
        val json = toCommonFormat(tempFile)
        output.write(json)
        end - start
    }

    private def toCommonFormat(file: File) = {
        val lines = Files.readAllLines(Paths.get(file.getAbsolutePath)).asScala.toList
        val nodeMap: mutable.Map[String, Node] = mutable.Map()
        val edgeArray: mutable.ListBuffer[Edge] = mutable.ListBuffer()
        for (line <- lines) {
            println(line)
            val call = line.split("->")
            val caller = parseNode(call(0).trim)
            val callee = parseNode(call(1).trim)
            nodeMap += (caller.id -> caller)
            edgeArray += Edge(source = caller, target = callee)
        }

        println(s"NODES: ${nodeMap.values.mkString("\n")}")
        println(s"EDGES: ${edgeArray.mkString("\n")}")
        val jsonCG = write(edgeArray)
        jsonCG
    }

    private def parseNode(nodeStr: String): Node = {
        var resultStr = ""
        var fileName = nodeStr.split("\\[")(0).trim
        if(fileName == "js") fileName = "global"
        if(fileName.endsWith(".js")) fileName = fileName.substring(0, fileName.length - 3)

        var identifier = nodeStr.split("\\[")(1).trim
        val source_meta = identifier.split(":")  // foo:file.js:1:2
        identifier = source_meta(0) // contains function name

        val start = source_meta(2).toInt

        Node(id = fileName + "::" + identifier,
            label = identifier,
            file = source_meta(1),
            start = Position(row = start))
    }
}

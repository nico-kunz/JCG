import java.io.File

object JSCallGraphAdapter extends TestAdapter {
    val debug: Boolean = false

    val possibleAlgorithms: Array[String] = Array("NONE", "ONESHOT", "DEMAND", "FULL")
    val frameworkName: String = "js-callgraph"

    def main(args: Array[String]): Unit = {
        // generate call graphs for all algorithms
        for (algo <- possibleAlgorithms) {
            generateCallGraphs(s"results/js/$frameworkName", algo)
        }
    }

    private def generateCallGraphs(outputDirPath: String, algorithm: String): Unit = {
        val outputDir = new File(s"$outputDirPath/$algorithm")
        outputDir.mkdirs()
        val inputDirs = new File("testcasesOutput/js").list()

        // check if js-callgraph command is available
        val command = "js-callgraph"
        try {
            sys.process.Process(Seq(command, "-h")).!!
        } catch {
            case e: Exception => {
                println(s"${Console.RED}[Error]: $command command not found, make sure it is installed and in your PATH${Console.RESET}")
                return
            }
        }

        // generate callgraph for every testcase
        inputDirs.foreach(inputDir => {
            val inputDirPath = s"testcasesOutput/js/$inputDir"
            val outputPath = s"${outputDir.getAbsolutePath}/$inputDir.json"
            val args = Seq("--cg", inputDirPath, "--output", outputPath, "--strategy", algorithm)
            println(s"[DEBUG] executing ${(Seq(command) ++ args).mkString(" ")}")
            sys.process.Process(Seq(command) ++ args).!!
        })

        println("Call graphs generated!")
    }
}

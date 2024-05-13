import scala.io.Source
import java.io.PrintWriter
import java.io.File

import javax.tools.ToolProvider
import org.apache.commons.io.FileUtils
import play.api.libs.json.JsValue
import play.api.libs.json.Json

/**
 * This tool searches for '.md' files in the resources (or in the specified directory) to extracts
 * the test cases.
 * The extraction include, compiling, packaging and running (in order to find errors).
 * For each extracted test case, a project specification file ([[ProjectSpecification]])
 * is generated.
 *
 * @author Michael Reif
 * @author Florian Kuebler
 */
object TestCaseExtractor {

    val pathSeparator = File.pathSeparator
    val debug = false

    /**
     * Extracts the test cases.
     *
     * @param args possible arguments are:
     *             '--rsrcDir dir', where 'dir' is the directory to search in. If this option is not
     *             specified, the resource root directory will be used.
     *             '--md filter', where 'filter' is a prefix of the filenames to be included.
     */
    def main(args: Array[String]): Unit = {
        val userDir = System.getProperty("user.dir")
        val tmp = new File("tmp")
        val resultJars = new File("testcaseJars")

        // clean up existing directories
        FileOperations.cleanUpDirectory(tmp)
        FileOperations.cleanUpDirectory(resultJars)

        // parse arguments
        val mdFilter = getArgumentValue(args, "--md").getOrElse("")
        val resourceDir = new File(getArgumentValue(args, "--rsrcDir").getOrElse(userDir))

        val javaDir = new File(resourceDir, "java")
        val jsDir = new File(resourceDir, "js")

        // extract test cases
        val javaResources = FileOperations.listMarkdownFiles(javaDir, mdFilter)
        println(javaResources.mkString(", "))
        extractJavaTests(resultJars, javaResources)

        val jsResources = FileOperations.listMarkdownFiles(jsDir, mdFilter)
        println(jsResources.mkString(", "))
        extractJSTests(jsResources)

        FileUtils.deleteDirectory(tmp)
    }

    private def extractJSTests(resources: Array[File]): Unit = {
        val resultDir = new File("testcaseJS")

        // Clear result directory if it already exists
        FileOperations.cleanUpDirectory(resultDir)

        resources.foreach(file => {
            if (debug) {
                println(file)
            }

            // read lines from given testcase file
            val source = Source.fromFile(file)
            val lines = try source.mkString finally source.close()

            /*
             * ##ProjectName
             * [//]: # (Main: path/to/Main.js)
             * multiple code snippets
             * [//]: # (END)
             */
            val reHeaders = ("""(?s)""" +
              """\#\#(?<projectName>[^\n]*)\n""" + // ##ProjectName
              """\[//\]: \# \((?:MAIN: (?<mainClass>[^\n]*)|LIBRARY)\)\n""" + // [//]: # (Main: path.to.Main.js) or [//]: # (LIBRARY)
              """(?<body>.*?)""" + // multiple code snippets
              """\[//\]: \# \(END\)""").r // [//]: # (END)

            /*
             * (```json
             * ...
             * ```)?
             * ```js
             * // path/to/File.js
             * CODE SNIPPET
             * ```
             */
            val re = """(?s)```(json\n(?<expectedCG>[^`]*)```\n```)?js(\n// ?(?<packageName>[^/]*)(?<fileName>[^\n]*)\n(?<codeSnippet>[^`]*))```""".r

            reHeaders.findAllIn(lines).matchData.foreach(projectMatchResult => {
                val projectName = projectMatchResult.group("projectName").trim
                if (debug) {
                    println("[DEBUG] project", projectName)
                }

                // create Folder for project
                val outputFolder = new File(resultDir, projectName)
                outputFolder.mkdirs()

                re.findAllIn(projectMatchResult.group("body")).matchData.foreach { matchResult =>
                    val packageName = matchResult.group("packageName")
                    val filePath = s"$projectName/$packageName${matchResult.group("fileName")}"
                    val codeSnippet = matchResult.group("codeSnippet")
                    val expectedCG = matchResult.group("expectedCG")

                    val codeFile = new File(resultDir.getAbsolutePath, filePath)
                    val cgFile = new File(resultDir.getAbsolutePath, s"$projectName/$packageName${matchResult.group("fileName")}on")
                    codeFile.getParentFile.mkdirs()
                    codeFile.createNewFile()
                    FileOperations.writeToFile(codeFile, codeSnippet)

                    if (expectedCG != null) {
                        cgFile.getParentFile.mkdirs()
                        cgFile.createNewFile()
                        FileOperations.writeToFile(cgFile, expectedCG)
                    }

                    codeFile.getAbsolutePath
                }
            })
        })
    }


    private def extractJavaTests(result: File, resources: Array[File]): Unit = {
        val tmp = new File("tmp")
        resources.foreach { sourceFile ⇒
            if (debug) {
                println(sourceFile)
            }
            val source = Source.fromFile(sourceFile)
            val lines = try source.mkString finally source.close()

            /*
             * ##ProjectName
             * [//]: # (Main: path/to/Main.java)
             * multiple code snippets
             * [//]: # (END)
             */
            val reHeaders = ("""(?s)""" +
              """\#\#([^\n]*)\n""" + // ##ProjectName
              """\[//\]: \# \((?:MAIN: ([^\n]*)|LIBRARY)\)\n""" + // [//]: # (Main: path.to.Main.java) or [//]: # (LIBRARY)
              """(.*?)""" + // multiple code snippets
              """\[//\]: \# \(END\)""").r( // [//]: # (END)
                "projectName", "mainClass", "body"
            )
            /*
             * ```java
             * // path/to/Class.java
             * CODE SNIPPET
             * ```
             */
            val re = """(?s)```java(\n// ([^/]*)([^\n]*)\n([^`]*))```""".r

            reHeaders.findAllIn(lines).matchData.foreach { projectMatchResult ⇒
                val projectName = projectMatchResult.group("projectName").trim
                val main = projectMatchResult.group("mainClass")
                assert(main == null || !main.contains("/"), "invalid main class, use '.' instead of '/'")
                val srcFiles = re.findAllIn(projectMatchResult.group("body")).matchData.map { matchResult ⇒
                    val packageName = matchResult.group(2)
                    val fileName = s"$projectName/src/$packageName${matchResult.group(3)}"
                    val codeSnippet = matchResult.group(4)

                    val file = new File(tmp.getAbsolutePath, fileName)
                    file.getParentFile.mkdirs()
                    file.createNewFile()

                    val pw = new PrintWriter(file)
                    pw.write(codeSnippet)
                    pw.close()
                    file.getAbsolutePath
                }

                val compiler = ToolProvider.getSystemJavaCompiler

                val bin = new File(tmp.getAbsolutePath, s"$projectName/bin/")
                bin.mkdirs()

                val targetDirs = FileOperations.findJCGTargetDirs()
                val classPath = Seq(".", FileOperations.targetDirsToCP(targetDirs), System.getProperty("java.home")).mkString(s"$pathSeparator")

                val compilerArgs = Seq("-cp", s"$classPath", "-d", bin.getAbsolutePath, "-encoding", "UTF-8", "-source", "1.8", "-target", "1.8") ++ srcFiles

                if (debug) {
                    println(compilerArgs.mkString("[DEBUG] Compiler args: \n\n", "\n", "\n\n"))
                }

                compiler.run(null, null, null, compilerArgs: _*)

                val allClassFiles = FileOperations.recursiveListFiles(bin.getAbsoluteFile)

                if (debug) {
                    println(allClassFiles.mkString("[DEBUG] Produced class files: \n\n", "\n", "\n\n"))
                }

                val allClassFileNames = allClassFiles.map(_.getAbsolutePath.replace(s"${tmp.getAbsolutePath}/$projectName/bin/", ""))


                val jarOpts = Seq(if (main != null) "cfe" else "cf")
                val outPathCompiler = new File(s"${result.getAbsolutePath}/$projectName.jar")
                val mainOpt = Option(main)
                val args = Seq("jar") ++ jarOpts ++ Seq(outPathCompiler.getAbsolutePath) ++ mainOpt ++ allClassFileNames

                if (debug) {
                    println(args.mkString("[DEBUG] Jar args: \n\n", "\n", "\n\n"))
                }

                val exitCode = sys.process.Process(args, bin).!

                if (debug && exitCode != 0) {
                    println(s"[DEBUG] EXIT CODE $exitCode")
                }

                if (main != null) {
                    println(s"running $projectName.jar")
                    sys.process.Process(Seq("java", "-jar", s"$projectName.jar"), result).!
                }

                val projectSpec = ProjectSpecification(
                    name = projectName,
                    target = new File(outPathCompiler.getAbsolutePath).getCanonicalPath,
                    main = mainOpt,
                    java = 8,
                    cp = None
                )

                val projectSpecJson: JsValue = Json.toJson(projectSpec)
                val projectSpecOut = new File(result, s"$projectName.conf")
                val pw = new PrintWriter(projectSpecOut)
                pw.write(Json.prettyPrint(projectSpecJson))
                pw.close()
            }
        }
    }


    /**
     * Returns the value of the given CLI argument.
     * If the argument is not found, None is returned.
     *
     * @param args array holding the arguments to search in.
     */
    def getArgumentValue(args: Array[String], argName: String): Option[String] = {
        args.sliding(2, 2).collectFirst({
            case Array(`argName`, value: String) => value
        })
    }
}
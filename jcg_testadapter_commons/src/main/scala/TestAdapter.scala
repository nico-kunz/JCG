trait TestAdapter {
    val frameworkName: String
    val language: String

    def possibleAlgorithms: Array[String]

    def serializeCG(algorithm: String, outputDirPath: String, inputDirPath: String): Long

    def serializeAllCGs(inputDirPath: String, outputDirPath: String): Unit
}

# Java Call Graph (JCG)
This repository accommodates the Java Call Graph (JCG) project, a collection of annotated test cases
that are relevant for call-graph construction in Java. The test cases specifically target the call
graph's soundness and, therefore, tests the correct support of Java language features, core APIs, and
runtime (JVM) callbacks. Not supporting those features/APIs will render the call graph unsound.

## The Annotations Project
The subproject `jcg_annotations` provides the core of the JCG test suite.
It contains the annotation classes that are used to specify expectations of the call graph that represent
the ground truth.

Furthermore, the set of test cases, that are shipped with JCG, are located in the `src/main/resources`
directory as markdown `.md` files.

In addition to the specification of the test cases, the `TestCaseExtractor` retrieves, compiles, bundle and
runs the Java code for each test case in each markdown file.

## Serialization of the CallGraph
In `jcg_testadapter_commons` we provide the following interface that should be used in order to apply a
new call graph creation framework:

```java
public interface JCGTestAdapter {
    void serializeCG(String algorithm, String target, String classPath, String outputFile) throws Exception;
    String[] possibleAlgorithms();
    String frameworkName();
}
```

For each Frameworg, e.g. OPAL, WALA, Soot, there should be one test adapter implementation.
A call to `serialize` should execute the specific call graph algorithm and write a JSon file containing
the serialized version of the computed callgraph in the following format:

```json
{ "callSites": [
    { "declaredTarget": {
        "name": "getDeclaredMethod",
        "parameterTypes": ["Ljava/lang/String;","[Ljava/lang/Class;"],
        "returnType": "Ljava/lang/reflect/Method;",
        "declaringClass": "Ljava/lang/Class;" },
      "method": {
        "name": "main",
        "parameterTypes": ["[Ljava/lang/String;"],
        "returnType": "V",
        "declaringClass": "Ltr1/Foo;" },
      "line": 12,
      "targets": [ {
          "name": "getDeclaredMethod",
          "parameterTypes": ["Ljava/lang/String;","[Ljava/lang/Class;"],
          "returnType": "Ljava/lang/reflect/Method;",
          "declaringClass": "Ljava/lang/Class;" } ]
    },
    ... ]
}
```

A serialized call graph contains of an array of the `callSites` that are contained in the target code.
Each call site consists of a JSon object representing the `declaredTarget` method, and the `method`
containing the call site together with the `line` number. Furthermore, the `targets` array specifies the methods
that are identified as call targets in the computed call graph.

Each method object is defined, using a `name`, `returnType`, `parameterTypes` and a `declaringClass`.
All types must be given in JVM binary notation (object types start with `L`, ends with `;` and `/` is used in packages
instead of a `.`).


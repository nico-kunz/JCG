    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileWriter;
    import java.io.IOException;
    import java.io.Writer;
    import java.net.URL;
    import java.util.ArrayList;
    import java.util.Iterator;
    import java.util.Map;
    import java.util.Set;

    import com.ibm.wala.cast.js.loader.JavaScriptLoader;
    import com.ibm.wala.cast.js.translator.CAstRhinoTranslatorFactory;
    import com.ibm.wala.cast.js.types.JavaScriptMethods;
    import com.ibm.wala.cast.js.util.CallGraph2JSON;
    import com.ibm.wala.cast.loader.AstMethod;
    import com.ibm.wala.cast.tree.CAstSourcePositionMap.Position;
    import com.ibm.wala.cast.types.AstMethodReference;
    import com.ibm.wala.classLoader.CallSiteReference;
    import com.ibm.wala.classLoader.IMethod;
    import com.ibm.wala.cast.js.util.JSCallGraphBuilderUtil;
    import com.ibm.wala.ipa.callgraph.CGNode;
    import com.ibm.wala.ipa.callgraph.CallGraph;
    import com.ibm.wala.util.CancelException;
    import com.ibm.wala.util.WalaException;
    import com.ibm.wala.util.collections.HashMapFactory;
    import com.ibm.wala.util.collections.MapUtil;
    import java.util.function.Function;

    // Code mostly from com.ibm.wala.cast.js/source/com/ibm/wala/cast/js/util/CallGraph2JSON.java
    // https://github.com/wala/WALA/blob/0286c2b0487735176fb98c526a2eddc442c324f1/com.ibm.wala.cast.js/source/com/ibm/wala/cast/js/util/CallGraph2JSON.java
    public class WalaConverter {

        public static void main(String[] args)
                throws IllegalArgumentException, IOException, CancelException, WalaException {
            System.out.println("WalaConverter started...");
            if (args.length < 1) {
                System.out.println("Usage: WalaConverter <folder> ");
                System.exit(1);
            }

            String folder = args[0];
            String outputFolder = args[1];

            Writer wStat = new BufferedWriter(new FileWriter(new File(outputFolder + "/runtime_memory_stats.txt")));

            File folderF = new File(args[0]);
            System.out.println("Processing files in folder: " + folderF.getAbsolutePath());
            System.out.println(folderF.listFiles().length);
            for (File f : folderF.listFiles()) {
                System.out.println("Start loop");
                if(!!f.toString().contains("raytrace")) {
                    System.out.println("Skipping file: " + f.getName());
                    continue;
                }
                System.out.println(f);
                if (!f.isFile() || !f.toString().endsWith(".js")) {
                    System.out.println("Skipping non-JS file: " + f.getName());
                    continue;
                }
                Runtime rt = Runtime.getRuntime();
                rt.gc();
                System.out.println("Generating call graph for file: " + f.getName());

                long beforeUsedMem=rt.totalMemory()-rt.freeMemory();
                long startTime = System.currentTimeMillis();
                com.ibm.wala.cast.js.ipa.callgraph.JSCallGraphUtil.setTranslatorFactory(new CAstRhinoTranslatorFactory());

                // CallGraph CG = builder.makeCallGraph(builder.getOptions());
                CallGraph CG;
                try {
                    System.out.println("Making call graph...");
                    CG = JSCallGraphBuilderUtil.makeScriptCG(args[0], f.getName());
                } catch (Exception e) {
                    System.out.println("Error before making call graph: " + e.getMessage());
                    e.printStackTrace();
                    continue;
                }

                CallGraph2JSON callGraph2JSON = new CallGraph2JSON();
                String s = serialize(CG, folder);

                try (Writer writer = new BufferedWriter(new FileWriter(new File(outputFolder + "/" + f.getName().replace(".js", ".cgtxt"))))) {
                    writer.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                long afterUsedMem=rt.totalMemory()-rt.freeMemory();
                long stopTime = System.currentTimeMillis();
                long elapsedTime = stopTime - startTime;
                long actualMemUsed=afterUsedMem-beforeUsedMem;
                wStat.write(f.toString() + ";" + actualMemUsed + " B;" + elapsedTime/1000.0f + " sec;\n");
                System.out.println(f.toString() + ";" + actualMemUsed + " B;" + elapsedTime/1000.0f + " sec;\n");
                System.out.println(f.toString() + " is finished!");
                wStat.flush();
                System.out.flush();
            }
            wStat.close();

        }

        public static String serialize(CallGraph cg, String base_folder) {
            Map<String, Set<String>> edges = extractEdges(cg, base_folder);
            return toDotFormat(edges);
        }

        public static Map<String, Set<String>> extractEdges(CallGraph cg, String base_folder) {
            Map<String, Set<String>> edges = HashMapFactory.make();
            for (CGNode nd : cg) {
                if (!isRealFunction(nd.getMethod()))
                    continue;

                AstMethod method = (AstMethod) nd.getMethod();


                for (Iterator<CallSiteReference> iter = nd.iterateCallSites(); iter.hasNext();) {

                    CallSiteReference callsite = iter.next();

                    Set<IMethod> targets = com.ibm.wala.util.collections.Util.mapToSet(cg.getPossibleTargets(nd, callsite),
                            new Function<CGNode, IMethod>() {
                                @Override
                                public IMethod apply(CGNode nd) {
                                    return nd.getMethod();
                                }
                            });

                    serializeCallSite(method, callsite, targets, edges, base_folder);
                }
            }
            return edges;
        }

        public static void serializeCallSite(AstMethod method, CallSiteReference callsite, Set<IMethod> targets,
                Map<String, Set<String>> edges, String base_folder) {
            Set<String> targetNames = MapUtil.findOrCreateSet(edges,
                    ppPos(method, method.getSourcePosition(callsite.getProgramCounter()), base_folder));
            for (IMethod target : targets) {
                target = getCallTargetMethod(target);
                if (!isRealFunction(target))
                    continue;

                targetNames.add(ppPos((AstMethod) target, ((AstMethod) target).getSourcePosition(), base_folder));
            }
        }

        private static IMethod getCallTargetMethod(IMethod method) {
            if (method.getName().equals(JavaScriptMethods.ctorAtom)) {
                method = method.getDeclaringClass().getMethod(AstMethodReference.fnSelector);
                if (method != null)
                    return method;
            }
            return method;
        }

        public static boolean isRealFunction(IMethod method) {
            if (method instanceof AstMethod) {
                String methodName = method.getDeclaringClass().getName().toString();

                // exclude synthetic DOM modelling functions
                if (methodName.contains("/make_node"))
                    return false;

                for (String bootstrapFile : JavaScriptLoader.bootstrapFileNames)
                    if (methodName.startsWith("L" + bootstrapFile + "/"))
                        return false;

                return method.getName().equals(AstMethodReference.fnAtom);
            }
            return false;
        }


        private static String ppPos(AstMethod method, Position pos, String base_folder) {
            if (pos == null) {
                return "unknown.js[unknown:1:1]";
            }

            URL url = pos.getURL();
            String file = url.getFile();
            file = file.substring(file.lastIndexOf('/') + 1);

            // --- Extract function name ---
            String funcName = "toplevel";

            if (method instanceof AstMethod.Retranslatable) {
                AstMethod.Retranslatable retr = (AstMethod.Retranslatable) method;
                if (retr.getEntity() != null && retr.getEntity().getName() != null) {
                    funcName = retr.getEntity().getName();
                }
            } else {
                funcName = method.getName().toString();
            }

            // --- Clean up anonymous/auto-generated names ---
            if (funcName == null || funcName.isEmpty() || funcName.equals("do") ||
                    funcName.startsWith(file) || funcName.contains("@")) {
                funcName = "anon";
            }

            Position posi = method.getSourcePosition();
            if (posi == null) {
                posi = pos;
            }

            return String.format(
                    "%s[%s:%s:%d:%d]",
                    file,
                    funcName,
                    file,
                    posi.getFirstLine(),
                    posi.getFirstCol() + 2
            );
        }



        public static String toDotFormat(Map<String, Set<String>> map) {
            StringBuffer res = new StringBuffer();

            res.append(joinWith(com.ibm.wala.util.collections.Util.mapToSet(map.entrySet(),
                    new Function<Map.Entry<String, Set<String>>, String>() {
                        @Override
                        public String apply(Map.Entry<String, Set<String>> e) {
                            StringBuffer res = new StringBuffer();
                            if (e.getValue().size() > 0) {
                                String k = e.getKey();

                                res.append(joinWith(com.ibm.wala.util.collections.Util.mapToSet(e.getValue(),
                                        new Function<String, String>() {
                                            @Override
                                            public String apply(String str) {
                                                return k + " -> " + str;
                                            }
                                        }), "\n"));
                                res.append("\n");
                            }
                            return res.length() == 0 ? "" : res.toString();
                        }
                    }), ""));

            return res.toString();
        }

        private static String joinWith(Iterable<String> lst, String sep) {
            StringBuffer res = new StringBuffer();
            ArrayList<String> strings = new ArrayList<String>();
            for (String s : lst)
                if (s != null)
                    strings.add(s);

            boolean fst = true;
            for (String s : strings) {
                if (fst)
                    fst = false;
                else
                    res.append(sep);
                res.append(s);
            }
            return res.toString();
        }
    }

package nl.stokpop.byteman;

import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleGeneratorTraceCallDuration {

    private static final String template = "RULE @className@ @methodName@ entry\n" +
        "CLASS @classSignature@\n" +
        "METHOD @methodSignature@\n" +
        "AT ENTRY\n" +
        "BIND thread = Thread.currentThread();\n" +
        "startTime = System.nanoTime()\n" +
        "IF true\n" +
        "DO link(\"@methodName@.@classSignature@\", thread, startTime)\n" +
        "ENDRULE\n" +
        "\n" +
        "RULE @className@ @methodName@ exit\n" +
        "CLASS @classSignature@\n" +
        "METHOD @methodSignature@\n" +
        "AT EXIT\n" +
        "BIND thread = Thread.currentThread();\n" +
        "startTime:long = unlink(\"@methodName@.@classSignature@\", thread);\n" +
        "endTime = System.nanoTime()\n" +
        "IF true\n" +
        "DO traceln(\"[BYTEMAN] [\" + thread.getName() + \"] @className@.@methodName@() elapsedTimeNanos = \" + (endTime - startTime))\n" +
        "ENDRULE";

    public static void main(String[] args) {
        List<ClassInfo> classInfos = new ArrayList<>();

        classInfos.add(ClassInfo.builder()
            .className("MakePdfService")
            .classSignature("nl.stokpop.server.MakePdfService")
            .methodName("addHelloWorldPage")
            .methodSignature("addHelloWorldPage(org.apache.pdfbox.pdmodel.PDDocument)")
            .build());

        classInfos.add(ClassInfo.builder()
            .className("MakePdfService")
            .classSignature("nl.stokpop.server.MakePdfService")
            .methodName("createHelloWorldPdf")
            .methodSignature("createHelloWorldPdf(org.apache.pdfbox.pdmodel.PDDocument)")
            .build());

        List<String> rules = classInfos.stream()
                .map(c -> template
                    .replaceAll("@className@", c.getClassName())
                    .replaceAll("@classSignature@", c.getClassSignature())
                    .replaceAll("@methodName@", c.getMethodName())
                    .replaceAll("@methodSignature@", c.getMethodSignature()))
                .collect(Collectors.toList());

        rules.stream().map(r -> r + "\n").forEach(System.out::println);
    }

    @Builder
    @Value
    static
    class ClassInfo {
        String className;
        String classSignature;
        String methodName;
        String methodSignature;
    }


}

package nl.stokpop.byteman;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RuleGenerator {

    private static final String template = "RULE ##name##\n" +
            "CLASS java.lang.String\n" +
            "METHOD <init>##params##\n" +
            "AT EXIT\n" +
            "IF NOT callerEquals(\"<init>\") \n" +
            "DO traceln(\"create string \" + $0);\n" +
            "incrementCounter(\"string creates\")\n" +
            "ENDRULE";

    private static final String constructors = "(char[])\n" +
            "(byte[])\n" +
            "(char[],int,int)\n" +
            "(int[],int,int)\n" +
            "(byte[],int,int,int)\n" +
            "(byte[],int)\n" +
            "(byte[],int,int,String)\n" +
            "(byte[],int,int,Charset)\n" +
            "(byte[],String)\n" +
            "(byte[],Charset)\n" +
            "(byte[],int,int)\n" +
            "(StringBuffer)\n" +
            "(StringBuilder)\n" +
            "(char[],boolean)\n";

    public static void main(String[] args) {
        List<String> params =
                Stream.of(constructors.split("\n"))
                        .collect(Collectors.toList());

        List<String> rules = params.stream()
                .map(param -> template.replace("##params##", param).replace("##name##", "count string create " + param))
                .collect(Collectors.toList());

        rules.stream().map(r -> r + "\n").forEach(System.out::println);
    }
}

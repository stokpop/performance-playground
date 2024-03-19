package nl.stokpop.classpath;

import java.security.CodeSource;
import java.security.ProtectionDomain;

public class CheckClass {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Provide a classname");
            System.exit(123);
        }
        String className = args[0];
        findClass(className);
    }

    private static void findClass(String className) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Not found: " + e);
            System.exit(123);
        }
        ProtectionDomain protectionDomain = aClass.getProtectionDomain();
        ClassLoader classLoader = aClass.getClassLoader();
        String classLoaderName = classLoader == null ? "Bootstrap" : classLoader.getName();
        CodeSource codeSource = protectionDomain.getCodeSource();
        String location = codeSource == null ? "unknown" : String.valueOf(codeSource.getLocation());
        System.out.printf("Found '%s'%nin classloader '%s'%nhere: '%s'%n", className, classLoaderName, location);
    }
}

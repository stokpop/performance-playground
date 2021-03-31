package nl.stokpop.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Questions:
 * - what will this code output?
 * - what is the difference between a static ThreadLocal and a non-static ThreadLocal?
 */
public class ThreadLocalFrenzy {

    public static void main(String[] args) throws InterruptedException {

        boolean runStatic = args.length == 0 || args[0].equals("static");

        if (runStatic) {
            print("--> Static test");
            staticTest();
        }
        else {
            print("--> Non static test");
            nonStaticTest();
        }
    }

    private static void staticTest() {
        ExecutorService service = Executors.newFixedThreadPool(3);

        service.submit(createSetTLRunnable("Peter"));
        service.submit(createReadTLRunnable());

        service.submit(createSetTLRunnable("Jan"));
        service.submit(createReadTLRunnable());

        service.submit(createSetTLRunnable("Anne"));
        service.submit(createReadTLRunnable());

        service.submit(createSetTLRunnable("Jo"));
        service.submit(createReadTLRunnable());

        service.submit(createReadTLRunnable());
        service.submit(createReadTLRunnable());
        service.submit(createReadTLRunnable());
        service.submit(createReadTLRunnable());

        service.shutdown();
    }

    private static void nonStaticTest() {

        UserContext c1 = new UserContext("c1");
        UserContext c2 = new UserContext("c2");

        ExecutorService service = Executors.newFixedThreadPool(3);

        service.submit(createSetTLContextRunnable("Peter", c1));
        service.submit(createReadTLContextRunnable(c1));

        service.submit(createSetTLContextRunnable("Jan", c1));
        service.submit(createReadTLContextRunnable(c1));

        service.submit(createSetTLContextRunnable("Anne", c2));
        service.submit(createReadTLContextRunnable(c2));

        service.submit(createSetTLContextRunnable("Jo", c2));
        service.submit(createReadTLContextRunnable(c2));

        service.submit(createReadTLContextRunnable(c1));
        service.submit(createReadTLContextRunnable(c1));
        service.submit(createReadTLContextRunnable(c2));
        service.submit(createReadTLContextRunnable(c2));

        service.shutdown();
    }

    private static Runnable createSetTLRunnable(String name) {
        return () -> {
            UserContextStatic.setUser(name);
            print(threadName() + " sets  " + UserContextStatic.getUser());
        };
    }

    private static Runnable createReadTLRunnable() {
        return () -> print(threadName() + " reads " + UserContextStatic.getUser());
    }


    private static Runnable createSetTLContextRunnable(String name, UserContext context) {
        return () -> {
            context.setUser(name);
            print(threadName() + " sets  " + context + " " + context.getUser());
        };
    }

    private static Runnable createReadTLContextRunnable(UserContext context) {
        return () -> print(threadName() + " reads " + context + " " + context.getUser());
    }

    private static String threadName() {
        return Thread.currentThread().getName();
    }

    private static void print(String text) {
        System.out.println(text);
    }
}

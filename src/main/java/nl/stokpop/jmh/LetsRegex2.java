package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import java.util.regex.Pattern;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsRegex2 {

    @Param({"  app \t\t  weird \u000b metrics\r\rtps\f\f1234 ", "Foo Bar"," Foo Bar ", "Foo Bar ", "  Foo Bar  ", "Foo@Bar", "Foó Bar", "||ó/.", "${Foo:Bar:baz}", "St. Foo's of Bar", "(Foo and (Bar and (Baz)))", "Foo.bar.baz", "FooBar"})
    String text;

//    @Param({"  app \t\t  weird \u000b metrics\r\rtps\f\f1234 ", "hello.world"})
//    String text;

    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final String DASH = "-";

    /**
     * Trims the string and replaces all whitespace characters with the provided symbol
     */
    static String sanitize(String string) {
        return WHITESPACE.matcher(string.trim()).replaceAll(DASH);
    }

    @Benchmark
    public void sanitizeRegexp(Blackhole blackhole) {
        blackhole.consume(sanitize(text));
    }

    @Benchmark
    public void sanitizeUtil(Blackhole blackhole) {
        blackhole.consume(replaceWhitespaceWithChar(text, '-'));
    }

    public static String replaceWhitespaceWithChar(String text, char replace) {
        if (text == null || text.length() == 0) {
            return text;
        }

        final char[] oldChars = text.toCharArray();
        char[] newChars = null;

        int indexNew = 0;
        boolean firstMatch = true;

        for (int indexOld = 0; indexOld < oldChars.length; indexOld++) {
            char oldChar = oldChars[indexOld];
            // white  chars in \\s regexp are: [ \t\n\x0B\f\r]
            if (isWhiteSpace(oldChar)) {
                if (newChars == null) {
                    // only create new char array if it is needed
                    newChars = new char[text.length()];
                    System.arraycopy(oldChars, 0, newChars, 0, indexOld);
                }
                if (firstMatch) {
                    // only copy first of the found replace char
                    newChars[indexNew] = replace;
                    firstMatch = false;
                    indexNew++;
                }
                // else: skip same char in line
            }
            else {
                firstMatch = true;
                if (newChars != null) {
                    newChars[indexNew] = oldChar;
                }
                indexNew++;
            }
        }

        return trimPreAndPostReplacedChars(text, oldChars, newChars, indexNew);
    }

    private static String trimPreAndPostReplacedChars(String text, char[] oldChars, char[] newChars, int indexNew) {
        final boolean prefixWhites = isWhiteSpace(oldChars[0]);
        final boolean postfixWhites = isWhiteSpace(oldChars[oldChars.length - 1]);

        if (newChars == null) {
            return text;
        } else {
            int offset = prefixWhites ? 1 : 0;
            int count = indexNew - (postfixWhites ? 1 : 0) - offset;
            return new String(newChars, offset, count);
        }
    }

    private static boolean isWhiteSpace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\u000b' || c == '\f' || c == '\r';
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .threads(2)
                .warmupIterations(2)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("sanitize")
                .build();

        new Runner(options).run();
    }
}

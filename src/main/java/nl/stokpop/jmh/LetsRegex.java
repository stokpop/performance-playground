package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static org.springframework.util.StringUtils.hasLength;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
public class LetsRegex {

    public String text = "//sdl///kfjsdl/fka////sdl//kfjsadlkf/";
    public String textSanitized = "/sdl/kfjsdl/fka/sdl/kfjsadlkf/";
    public String A = "a";
    public String B = "b";

    public static String ONE_CHAR_COMPLEX_REGEXP = "(a+)";

    private static final String TWO_OR_MORE_SLASHES_REGEXP = "//+";
    private static final Pattern SLASH_PATTERN = Pattern.compile(TWO_OR_MORE_SLASHES_REGEXP);
    private static final Pattern SLASH_LAST_PATTERN = Pattern.compile("/$");
    private static final Pattern ONE_CHAR_PATTERN = Pattern.compile("a");
    private static final Pattern ONE_CHAR_COMPLEX_PATTERN = Pattern.compile(ONE_CHAR_COMPLEX_REGEXP);

    @Benchmark
    public void stringReplaceChar(Blackhole blackhole) {
        blackhole.consume(text.replace('/', '_'));
    }

    @Benchmark
    public void stringReplaceString(Blackhole blackhole)  {
        blackhole.consume(text.replace("/", "_"));
    }

    @Benchmark
    public void stringReplaceAllSingleChar(Blackhole blackhole)  {
        blackhole.consume(text.replaceAll(A, B));
    }

    @Benchmark
    public void stringReplaceAllSingleCharOnePattern(Blackhole blackhole)  {
        blackhole.consume(ONE_CHAR_PATTERN.matcher(text).replaceAll(B));
    }

    @Benchmark
    public void stringReplaceAllSingleCharOneComplexPattern(Blackhole blackhole)  {
        blackhole.consume(ONE_CHAR_COMPLEX_PATTERN.matcher(text).replaceAll(B));
    }

    @Benchmark
    public void stringReplaceAllSingleCharComplexPattern(Blackhole blackhole)  {
        blackhole.consume(text.replaceAll(ONE_CHAR_COMPLEX_REGEXP, B));
    }

    @Benchmark
    public void stringReplaceAll(Blackhole blackhole)  {
        blackhole.consume(text.replaceAll(TWO_OR_MORE_SLASHES_REGEXP, "/"));
    }

    @Benchmark
    public void regexpReplaceAll(Blackhole blackhole)  {
        blackhole.consume(SLASH_PATTERN.matcher(text).replaceAll("/"));
    }

    @Benchmark
    public void regexpReplaceAllLast(Blackhole blackhole)  {
        blackhole.consume(SLASH_LAST_PATTERN.matcher(textSanitized).replaceAll(""));
    }

    @Benchmark
    public void plainReplaceAllLast(Blackhole blackhole)  {
        blackhole.consume(textSanitized.endsWith("/") ? textSanitized.substring(0, textSanitized.length() - 1) : textSanitized);
    }

    @Benchmark
    public void utilReplaceMultipleCharsByOneChar(Blackhole blackhole)  {
        blackhole.consume(replaceMultipleCharsByOneChar(text, '/'));
    }

    @Benchmark
    public void utilReplaceTrimTrailingCharacter(Blackhole blackhole)  {
        blackhole.consume(StringUtils.trimTrailingCharacter(textSanitized, '/'));
    }

    public static String replaceMultipleCharsByOneChar(String text, char replace) {
        if (!hasLength(text)) {
            return text;
        }

        char[] newChars = new char[text.length()];
        char[] oldChars = text.toCharArray();

        int indexNew = 0;
        boolean firstMatch = true;

        for (int indexOld = 0; indexOld < text.length(); indexOld++) {
            if (oldChars[indexOld] == replace) {
                if (firstMatch) {
                    // only copy first of the found replace char
                    newChars[indexNew] = oldChars[indexOld];
                    indexNew++;
                    firstMatch = false;
                }
                // else: skip same char in line
            }
            else {
                firstMatch = true;
                newChars[indexNew] = oldChars[indexOld];
                indexNew++;
            }
        }

        return new String(newChars,0, indexNew);
    }

    public static void main(String[] args) throws Exception {
        final Options options = new OptionsBuilder()
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(4)
                .verbosity(VerboseMode.EXTRA)
                .include("Replace")
                //.include("ComplexPattern")
                .build();

        new Runner(options).run();
    }
}

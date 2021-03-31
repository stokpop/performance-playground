package nl.stokpop.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 1)
@Warmup(iterations = 4)
@Measurement(iterations = 4)
@BenchmarkMode(Mode.Throughput)
public class LetsStream {

    @Param({"100", "1000", "10000", "100000"})
    public int listSize;

    private List<Integer> numbers;

    @Setup(Level.Trial)
    public void setUp() {
        numbers = IntStream.range(1, listSize).boxed().collect(Collectors.toList());
    }

    @Benchmark
    public void plainStream(Blackhole blackhole) {
        blackhole.consume(numbers.stream().reduce(0, Integer::sum));
    }

    @Benchmark
    public void plainStreamParallel(Blackhole blackhole) {
        blackhole.consume(numbers.parallelStream().reduce(0, Integer::sum));
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}

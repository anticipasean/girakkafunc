package cyclops.container.vector;

import cyclops.container.Vector;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
public class VectorFilter {

    Vector<String> vector;
    io.vavr.collection.Vector<String> js;

    @Setup
    public void before() {
        vector = Vector.range(0,
                              1000)
                       .map(i -> "" + i);
        js = io.vavr.collection.Vector.range(0,
                                             1000)
                                      .map(i -> "" + i);

    }


    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    public void cyclopsOps() {
        vector.filter(i -> Integer.parseInt(i) % 2 == 0);

    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10)
    @Measurement(iterations = 10)
    @Fork(1)
    public void vavrOps() {
        js.filter(i -> Integer.parseInt(i) % 2 == 0);

    }


}

package cyclops.pattern;

import cyclops.container.foldable.Deconstructable;
import cyclops.container.foldable.Deconstructable.Deconstructable1;
import cyclops.container.foldable.Deconstructable.Deconstructable2;
import cyclops.container.foldable.Deconstructable.Deconstructable3;
import cyclops.container.foldable.Deconstructable.Deconstructable4;
import cyclops.container.foldable.Deconstructable.Deconstructable5;
import cyclops.container.foldable.Sealed2;
import cyclops.container.foldable.Sealed3;
import cyclops.container.foldable.Sealed4;
import cyclops.container.foldable.Sealed5;
import cyclops.container.foldable.SealedOr;
import cyclops.container.immutable.tuple.Tuple1;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.container.immutable.tuple.Tuple3;
import cyclops.container.immutable.tuple.Tuple4;
import cyclops.container.immutable.tuple.Tuple5;
import cyclops.pattern.Case.Any;
import cyclops.pattern.Case.Case2;
import cyclops.pattern.Matching.OptionalMatching;
import cyclops.pattern.Matching.PatternMatching;
import cyclops.pattern.Matching.PatternMatching3;
import cyclops.pattern.Matching.PatternMatching4;
import cyclops.pattern.Matching.PatternMatching5;
import cyclops.pattern.Matching.PatternMatchingOrNone;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Deprecated
public final class Api {

    private Api() {
    }

    public static <T> OptionalMatching<T> Match(final Optional<T> value) {
        return new OptionalMatching<T>(value);
    }

    public static <T> PatternMatching<T> Match(T value) {
        return new PatternMatching<>(value);
    }

    public static <T1, T2> Matching.PatternMatching2<T1, T2> MatchType(Sealed2<T1, T2> value) {
        return new Matching.PatternMatching2<>(value);
    }

    public static <T1, T2, T3> PatternMatching3<T1, T2, T3> MatchType(Sealed3<T1, T2, T3> value) {
        return new PatternMatching3<>(value);
    }

    public static <T1, T2, T3, T4> PatternMatching4<T1, T2, T3, T4> MatchType(Sealed4<T1, T2, T3, T4> value) {
        return new PatternMatching4<>(value);
    }

    public static <T1, T2, T3, T4, T5> PatternMatching5<T1, T2, T3, T4, T5> MatchType(Sealed5<T1, T2, T3, T4, T5> value) {
        return new PatternMatching5<>(value);
    }

    public static <T1> PatternMatchingOrNone<T1> MatchOr(SealedOr<T1> value) {
        return new PatternMatchingOrNone<>(value);
    }

    public static <T extends Deconstructable<T1>, T1> PatternMatching<T1> Match(T value) {
        return new PatternMatching<>(value.unapply());
    }

    public static <T extends Deconstructable1<T1>, T1> PatternMatching<Tuple1<T1>> Match(T value) {
        return new PatternMatching<>(value.unapply());
    }

    public static <T extends Deconstructable2<T1, T2>, T1, T2> PatternMatching<Tuple2<T1, T2>> Match(T value) {
        return new PatternMatching<>(value.unapply());
    }

    public static <T extends Deconstructable3<T1, T2, T3>, T1, T2, T3> PatternMatching<Tuple3<T1, T2, T3>> Match(T value) {
        return new PatternMatching<>(value.unapply());
    }

    public static <T extends Deconstructable4<T1, T2, T3, T4>, T1, T2, T3, T4> PatternMatching<Tuple4<T1, T2, T3, T4>> Match(T value) {
        return new PatternMatching<>(value.unapply());
    }

    public static <T extends Deconstructable5<T1, T2, T3, T4, T5>, T1, T2, T3, T4, T5> PatternMatching<Tuple5<T1, T2, T3, T4, T5>> Match(T value) {
        return new PatternMatching<>(value.unapply());
    }

    public static <T, R> Case<Optional<T>, R> Case(Supplier<R> supplier0,
                                                   Supplier<R> supplier1) {
        return new Case.CaseOptional<>(supplier0,
                                       supplier1);
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern,
                                         Function<T, R> supplier) {
        return new Case.Case0<>(pattern,
                                supplier);
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern,
                                         Supplier<R> supplier) {
        return new Case.Case0<>(pattern,
                                value -> supplier.get());
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         Function<T, R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2),
                                supplier);
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         Supplier<R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2),
                                value -> supplier.get());
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         CyclopsPattern<T> pattern3,
                                         Function<T, R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2)
                                        .and(pattern3),
                                supplier);
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         CyclopsPattern<T> pattern3,
                                         Supplier<R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2)
                                        .and(pattern3),
                                value -> supplier.get());
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         CyclopsPattern<T> pattern3,
                                         CyclopsPattern<T> pattern4,
                                         Function<T, R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2)
                                        .and(pattern3)
                                        .and(pattern4),
                                supplier);
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         CyclopsPattern<T> pattern3,
                                         CyclopsPattern<T> pattern4,
                                         Supplier<R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2)
                                        .and(pattern3)
                                        .and(pattern4),
                                value -> supplier.get());
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         CyclopsPattern<T> pattern3,
                                         CyclopsPattern<T> pattern4,
                                         CyclopsPattern<T> pattern5,
                                         Function<T, R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2)
                                        .and(pattern3)
                                        .and(pattern4)
                                        .and(pattern5),
                                supplier);
    }

    public static <T, R> Case<T, R> Case(CyclopsPattern<T> pattern1,
                                         CyclopsPattern<T> pattern2,
                                         CyclopsPattern<T> pattern3,
                                         CyclopsPattern<T> pattern4,
                                         CyclopsPattern<T> pattern5,
                                         Supplier<R> supplier) {
        return new Case.Case0<>(pattern1.and(pattern2)
                                        .and(pattern3)
                                        .and(pattern4)
                                        .and(pattern5),
                                value -> supplier.get());
    }

    public static <T, R> Case<T, R> Case(Function<? super T, ? extends R> fn) {
        return new Case.CaseFn(fn);
    }

    public static <T, R> Case<T, R> Case(Predicate<T> predicate,
                                         Function<T, R> supplier) {
        return new Case.Case0<>(predicate,
                                supplier);
    }

    public static <T, R> Case<T, R> Case(Predicate<T> predicate,
                                         Supplier<R> supplier) {
        return new Case.Case0<>(predicate,
                                value -> supplier.get());
    }

    public static <T1, T2, R> Case<Tuple2<T1, T2>, R> Case(Predicate<T1> predicate1,
                                                           Predicate<T2> predicate2,
                                                           Function<? super Tuple2<T1, T2>, ? extends R> fn) {
        return new Case2<>(predicate1,
                           predicate2,
                           fn);
    }

    public static <T1, T2, R> Case<Tuple2<T1, T2>, R> Case(Predicate<T1> predicate1,
                                                           Predicate<T2> predicate2,
                                                           Supplier<R> supplier) {
        return new Case2<>(predicate1,
                           predicate2,
                           value -> supplier.get());
    }

    public static <T1, T2, T3, R> Case<Tuple3<T1, T2, T3>, R> Case(Predicate<T1> predicate1,
                                                                   Predicate<T2> predicate2,
                                                                   Predicate<T3> predicate3,
                                                                   Function<Tuple3<T1, T2, T3>, R> fn) {
        return new Case.Case3<>(predicate1,
                                predicate2,
                                predicate3,
                                fn);
    }

    public static <T1, T2, T3, R> Case<Tuple3<T1, T2, T3>, R> Case(Predicate<T1> predicate1,
                                                                   Predicate<T2> predicate2,
                                                                   Predicate<T3> predicate3,
                                                                   Supplier<R> supplier) {
        return new Case.Case3<>(predicate1,
                                predicate2,
                                predicate3,
                                value -> supplier.get());
    }

    public static <T1, T2, T3, T4, R> Case<Tuple4<T1, T2, T3, T4>, R> Case(Predicate<T1> predicate1,
                                                                           Predicate<T2> predicate2,
                                                                           Predicate<T3> predicate3,
                                                                           Predicate<T4> predicate4,
                                                                           Function<Tuple4<T1, T2, T3, T4>, R> fn) {
        return new Case.Case4<>(predicate1,
                                predicate2,
                                predicate3,
                                predicate4,
                                fn);
    }

    public static <T1, T2, T3, T4, R> Case<Tuple4<T1, T2, T3, T4>, R> Case(Predicate<T1> predicate1,
                                                                           Predicate<T2> predicate2,
                                                                           Predicate<T3> predicate3,
                                                                           Predicate<T4> predicate4,
                                                                           Supplier<R> supplier) {
        return new Case.Case4<>(predicate1,
                                predicate2,
                                predicate3,
                                predicate4,
                                val -> supplier.get());
    }

    public static <T1, T2, T3, T4, T5, R> Case<Tuple5<T1, T2, T3, T4, T5>, R> Case(Predicate<T1> predicate1,
                                                                                   Predicate<T2> predicate2,
                                                                                   Predicate<T3> predicate3,
                                                                                   Predicate<T4> predicate4,
                                                                                   Predicate<T5> predicate5,
                                                                                   Function<Tuple5<T1, T2, T3, T4, T5>, R> fn) {
        return new Case.Case5<>(predicate1,
                                predicate2,
                                predicate3,
                                predicate4,
                                predicate5,
                                fn);
    }

    public static <T1, T2, T3, T4, T5, R> Case<Tuple5<T1, T2, T3, T4, T5>, R> Case(Predicate<T1> predicate1,
                                                                                   Predicate<T2> predicate2,
                                                                                   Predicate<T3> predicate3,
                                                                                   Predicate<T4> predicate4,
                                                                                   Predicate<T5> predicate5,
                                                                                   Supplier<R> supplier) {
        return new Case.Case5<>(predicate1,
                                predicate2,
                                predicate3,
                                predicate4,
                                predicate5,
                                val -> supplier.get());
    }

    public static <T, R> Any<T, R> Any(Function<T, R> fn) {
        return fn::apply;
    }

    public static <T, R> Any<T, R> Any(Supplier<R> supplier) {
        return value -> supplier.get();
    }

}

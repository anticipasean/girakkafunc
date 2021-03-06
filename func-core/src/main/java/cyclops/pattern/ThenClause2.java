package cyclops.pattern;

import cyclops.container.control.Option;
import cyclops.container.immutable.tuple.Tuple2;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author smccarron
 */
public interface ThenClause2<K, V, KI, VI> extends Clause<Tuple2<Tuple2<K, V>, Option<Tuple2<KI, VI>>>> {

    static <K, V, KI, VI> ThenClause2<K, V, KI, VI> of(Supplier<Tuple2<Tuple2<K, V>, Option<Tuple2<KI, VI>>>> valueSupplier) {
        return new ThenClause2<K, V, KI, VI>() {
            @Override
            public Tuple2<Tuple2<K, V>, Option<Tuple2<KI, VI>>> get() {
                return valueSupplier.get();
            }
        };
    }

    default <KO, VO> OrMatchClause2<K, V, KI, VI, KO, VO> then(BiFunction<KI, VI, Tuple2<KO, VO>> mapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().map2(kiviTupleOpt -> kiviTupleOpt.map(kiviTuple2 -> mapper.apply(kiviTuple2._1(),
                                                                                                                                  kiviTuple2._2())))
                                                                .fold((kvTuple2, kovoTuple2) -> kovoTuple2.toEither(Tuple2.of(kvTuple2,
                                                                                                                              Option.none())))));
    }

    default <KO, VO> OrMatchClause2<K, V, KI, VI, KO, VO> then(Function<KI, KO> keyMapper,
                                                               Function<VI, VO> valueMapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().map2(kiviTupleOpt -> kiviTupleOpt.map(kiviTuple2 -> kiviTuple2.map1(keyMapper)
                                                                                                                               .map2(valueMapper)))
                                                                .fold((kvTuple2, kovoTuple2) -> kovoTuple2.toEither(Tuple2.of(kvTuple2,
                                                                                                                              Option.none())))));
    }

    default <KO, VO> OrMatchClause2<K, V, KI, VI, KO, VO> then(Function<Tuple2<KI, VI>, Tuple2<KO, VO>> mapper) {
        return OrMatchClause2.of(() -> MatchResult2.of(subject().map2(kiviOptTuple -> kiviOptTuple.map(mapper))
                                                                .fold((kvTuple, koVoTupleOpt) -> koVoTupleOpt.toEither(Tuple2.of(subject()._1(),
                                                                                                                                 Option.none())))));

    }

    //    default <KO, VO> OrMatchClause2<K, V, KI, VI, KO, VO> thenMapConcat(Function<Tuple2<KI, VI>, ? extends Iterable<Tuple2<KO, VO>>> mapper) {
    //        return OrMatchClause2.of(() -> MatchResult2.of(subject().map2(kiviTupleOpt -> kiviTupleOpt.concatMap(mapper::apply))
    //                                                                .fold((kvTuple2, kovoTuple2) -> kovoTuple2.toEither(Tuple2.of(kvTuple2,
    //                                                                                                                              Option.none())))));
    //    }
    //
    //    default <KO, VO> OrMatchClause2<K, V, KI, VI, KO, VO> thenMapConcat(BiFunction<KI, VI, ? extends Iterable<Tuple2<KO, VO>>> mapper) {
    //        return OrMatchClause2.of(() -> MatchResult2.of(subject().map2(kiviTupleOpt -> kiviTupleOpt.concatMap(kiviTuple2 -> mapper.apply(kiviTuple2._1(),
    //                                                                                                                                        kiviTuple2._2())))
    //                                                                .fold((kvTuple2, kovoTuple2) -> kovoTuple2.toEither(Tuple2.of(kvTuple2,
    //                                                                                                                              Option.none())))));
    //    }

}

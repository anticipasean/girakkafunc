package cyclops.container.immutable.tuple;


import cyclops.container.transformable.To;
import cyclops.function.cacheable.Memoize;
import cyclops.function.companion.Comparators;
import cyclops.function.enhanced.Function7;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/*
  A Tuple implementation that can be lazyEither eager / strict or lazy
 */

public class Tuple7<T1, T2, T3, T4, T5, T6, T7> implements To<Tuple7<T1, T2, T3, T4, T5, T6, T7>>, Serializable,
                                                           Comparable<Tuple7<T1, T2, T3, T4, T5, T6, T7>> {

    private static final long serialVersionUID = 1L;
    private final T1 _1;
    private final T2 _2;
    private final T3 _3;
    private final T4 _4;
    private final T5 _5;
    private final T6 _6;
    private final T7 _7;

    public Tuple7(T1 t1,
                  T2 t2,
                  T3 t3,
                  T4 t4,
                  T5 t5,
                  T6 t6,
                  T7 t7) {
        _1 = t1;
        _2 = t2;
        _3 = t3;
        _4 = t4;
        _5 = t5;
        _6 = t6;
        _7 = t7;
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 value1,
                                                                                     T2 value2,
                                                                                     T3 value3,
                                                                                     T4 value4,
                                                                                     T5 value5,
                                                                                     T6 value6,
                                                                                     T7 value7) {
        return new Tuple7<>(value1,
                            value2,
                            value3,
                            value4,
                            value5,
                            value6,
                            value7);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> lazy(Supplier<? extends T1> supplier1,
                                                                                       Supplier<? extends T2> supplier2,
                                                                                       Supplier<? extends T3> supplier3,
                                                                                       Supplier<? extends T4> supplier4,
                                                                                       Supplier<? extends T5> supplier5,
                                                                                       Supplier<? extends T6> supplier6,
                                                                                       Supplier<? extends T7> supplier7) {
        return new Tuple7<T1, T2, T3, T4, T5, T6, T7>(null,
                                                      null,
                                                      null,
                                                      null,
                                                      null,
                                                      null,
                                                      null) {
            @Override
            public T1 _1() {
                return supplier1.get();
            }

            @Override
            public T2 _2() {
                return supplier2.get();
            }

            @Override
            public T3 _3() {
                return supplier3.get();
            }

            @Override
            public T4 _4() {
                return supplier4.get();
            }

            @Override
            public T5 _5() {
                return supplier5.get();
            }

            @Override
            public T6 _6() {
                return supplier6.get();
            }

            @Override
            public T7 _7() {
                return supplier7.get();
            }
        };
    }

    public T1 _1() {
        return _1;
    }

    public T2 _2() {
        return _2;
    }

    public T3 _3() {
        return _3;
    }

    public T4 _4() {
        return _4;
    }

    public T5 _5() {
        return _5;
    }

    public T6 _6() {
        return _6;
    }

    public T7 _7() {
        return _7;
    }

    public Tuple1<T1> first() {
        return Tuple.tuple(_1());
    }

    public Tuple1<T2> second() {
        return Tuple.tuple(_2());
    }

    public Tuple1<T3> third() {
        return Tuple.tuple(_3());
    }

    public Tuple1<T4> fourth() {
        return Tuple.tuple(_4());
    }

    public Tuple1<T5> fifth() {
        return Tuple.tuple(_5());
    }

    public Tuple1<T6> sixth() {
        return Tuple.tuple(_6());
    }

    public Tuple1<T7> seventh() {
        return Tuple.tuple(_7());
    }

    public <R1> R1 transform(Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R1> fn) {
        return fn.apply(_1(),
                        _2(),
                        _3(),
                        _4(),
                        _5(),
                        _6(),
                        _7());
    }

    public Tuple7<T1, T2, T3, T4, T5, T6, T7> eager() {
        return of(_1(),
                  _2(),
                  _3(),
                  _4(),
                  _5(),
                  _6(),
                  _7());
    }

    public Tuple7<T1, T2, T3, T4, T5, T6, T7> memo() {
        Tuple7<T1, T2, T3, T4, T5, T6, T7> host = this;
        return new Tuple7<T1, T2, T3, T4, T5, T6, T7>(null,
                                                      null,
                                                      null,
                                                      null,
                                                      null,
                                                      null,
                                                      null) {
            final Supplier<T1> memo1 = Memoize.memoizeSupplier(host::_1);
            final Supplier<T2> memo2 = Memoize.memoizeSupplier(host::_2);
            final Supplier<T3> memo3 = Memoize.memoizeSupplier(host::_3);
            final Supplier<T4> memo4 = Memoize.memoizeSupplier(host::_4);
            final Supplier<T5> memo5 = Memoize.memoizeSupplier(host::_5);
            final Supplier<T6> memo6 = Memoize.memoizeSupplier(host::_6);
            final Supplier<T7> memo7 = Memoize.memoizeSupplier(host::_7);

            @Override
            public T1 _1() {

                return memo1.get();
            }

            @Override
            public T2 _2() {
                return memo2.get();
            }

            @Override
            public T3 _3() {
                return memo3.get();
            }

            @Override
            public T4 _4() {
                return memo4.get();
            }

            @Override
            public T5 _5() {
                return memo5.get();
            }

            @Override
            public T6 _6() {
                return memo6.get();
            }

            @Override
            public T7 _7() {
                return memo7.get();
            }
        };
    }

    @Override
    public int compareTo(Tuple7<T1, T2, T3, T4, T5, T6, T7> o) {
        int result = Comparators.naturalOrderIdentityComparator()
                                .compare(_1(),
                                         o._1());
        if (result == 0) {
            result = Comparators.naturalOrderIdentityComparator()
                                .compare(_2(),
                                         o._2());
            if (result == 0) {
                result = Comparators.naturalOrderIdentityComparator()
                                    .compare(_3(),
                                             o._3());
                if (result == 0) {
                    result = Comparators.naturalOrderIdentityComparator()
                                        .compare(_4(),
                                                 o._4());
                    if (result == 0) {
                        result = Comparators.naturalOrderIdentityComparator()
                                            .compare(_5(),
                                                     o._5());
                        if (result == 0) {
                            result = Comparators.naturalOrderIdentityComparator()
                                                .compare(_6(),
                                                         o._6());
                            if (result == 0) {
                                result = Comparators.naturalOrderIdentityComparator()
                                                    .compare(_7(),
                                                             o._7());
                            }
                        }
                    }
                }
            }

        }
        return result;
    }

    public <R1, R2, R3, R4, R5, R6, R7> Tuple7<R1, R2, R3, R4, R5, R6, R7> mapAll(Function<? super T1, ? extends R1> fn1,
                                                                                  Function<? super T2, ? extends R2> fn2,
                                                                                  Function<? super T3, ? extends R3> fn3,
                                                                                  Function<? super T4, ? extends R4> fn4,
                                                                                  Function<? super T5, ? extends R5> fn5,
                                                                                  Function<? super T6, ? extends R6> fn6,
                                                                                  Function<? super T7, ? extends R7> fn7) {
        return of(fn1.apply(_1()),
                  fn2.apply(_2()),
                  fn3.apply(_3()),
                  fn4.apply(_4()),
                  fn5.apply(_5()),
                  fn6.apply(_6()),
                  fn7.apply(_7()));
    }

    public <R1, R2, R3, R4, R5, R6, R7> Tuple7<R1, R2, R3, R4, R5, R6, R7> lazyMapAll(Function<? super T1, ? extends R1> fn1,
                                                                                      Function<? super T2, ? extends R2> fn2,
                                                                                      Function<? super T3, ? extends R3> fn3,
                                                                                      Function<? super T4, ? extends R4> fn4,
                                                                                      Function<? super T5, ? extends R5> fn5,
                                                                                      Function<? super T6, ? extends R6> fn6,
                                                                                      Function<? super T7, ? extends R7> fn7) {
        return lazy(() -> (fn1.apply(_1())),
                    () -> fn2.apply(_2()),
                    () -> fn3.apply(_3()),
                    () -> fn4.apply(_4()),
                    () -> fn5.apply(_5()),
                    () -> fn6.apply(_6()),
                    () -> fn7.apply(_7()));
    }

    public <R> Tuple7<R, T2, T3, T4, T5, T6, T7> map1(Function<? super T1, ? extends R> fn) {
        return of(fn.apply(_1()),
                  _2(),
                  _3(),
                  _4(),
                  _5(),
                  _6(),
                  _7());
    }

    public <R> Tuple7<R, T2, T3, T4, T5, T6, T7> lazyMap1(Function<? super T1, ? extends R> fn) {
        return lazy(() -> fn.apply(_1()),
                    () -> _2(),
                    () -> _3(),
                    () -> _4(),
                    () -> _5(),
                    () -> _6(),
                    () -> _7());
    }

    public <R> Tuple7<T1, R, T3, T4, T5, T6, T7> map2(Function<? super T2, ? extends R> fn) {
        return of(_1(),
                  fn.apply(_2()),
                  _3(),
                  _4(),
                  _5(),
                  _6(),
                  _7());
    }

    public <R> Tuple7<T1, R, T3, T4, T5, T6, T7> lazyMap2(Function<? super T2, ? extends R> fn) {
        return lazy(() -> _1(),
                    () -> fn.apply(_2()),
                    () -> _3(),
                    () -> _4(),
                    () -> _5(),
                    () -> _6(),
                    () -> _7());
    }

    public <R> Tuple7<T1, T2, R, T4, T5, T6, T7> map3(Function<? super T3, ? extends R> fn) {
        return of(_1(),
                  _2(),
                  fn.apply(_3()),
                  _4(),
                  _5(),
                  _6(),
                  _7());
    }

    public <R> Tuple7<T1, T2, R, T4, T5, T6, T7> lazyMap3(Function<? super T3, ? extends R> fn) {
        return lazy(() -> _1(),
                    () -> _2(),
                    () -> fn.apply(_3()),
                    () -> _4(),
                    () -> _5(),
                    () -> _6(),
                    () -> _7());
    }

    public <R> Tuple7<T1, T2, T3, R, T5, T6, T7> map4(Function<? super T4, ? extends R> fn) {
        return of(_1(),
                  _2(),
                  _3(),
                  fn.apply(_4()),
                  _5(),
                  _6(),
                  _7());
    }

    public <R> Tuple7<T1, T2, T3, R, T5, T6, T7> lazyMap4(Function<? super T4, ? extends R> fn) {
        return lazy(() -> _1(),
                    () -> _2(),
                    () -> _3(),
                    () -> fn.apply(_4()),
                    () -> _5(),
                    () -> _6(),
                    () -> _7());
    }

    public <R> Tuple7<T1, T2, T3, T4, R, T6, T7> map5(Function<? super T5, ? extends R> fn) {
        return of(_1(),
                  _2(),
                  _3(),
                  _4(),
                  fn.apply(_5()),
                  _6(),
                  _7());
    }

    public <R> Tuple7<T1, T2, T3, T4, R, T6, T7> lazyMap5(Function<? super T5, ? extends R> fn) {
        return lazy(() -> _1(),
                    () -> _2(),
                    () -> _3(),
                    () -> _4(),
                    () -> fn.apply(_5()),
                    () -> _6(),
                    () -> _7());
    }

    public <R> Tuple7<T1, T2, T3, T4, T5, R, T7> map6(Function<? super T6, ? extends R> fn) {
        return of(_1(),
                  _2(),
                  _3(),
                  _4(),
                  _5(),
                  fn.apply(_6()),
                  _7());
    }

    public <R> Tuple7<T1, T2, T3, T4, T5, R, T7> lazyMap6(Function<? super T6, ? extends R> fn) {
        return lazy(() -> _1(),
                    () -> _2(),
                    () -> _3(),
                    () -> _4(),
                    () -> _5(),
                    () -> fn.apply(_6()),
                    () -> _7());
    }

    public <R> Tuple7<T1, T2, T3, T4, T5, T6, R> map7(Function<? super T7, ? extends R> fn) {
        return of(_1(),
                  _2(),
                  _3(),
                  _4(),
                  _5(),
                  _6(),
                  fn.apply(_7()));
    }

    public <R> Tuple7<T1, T2, T3, T4, T5, T6, R> lazyMap7(Function<? super T7, ? extends R> fn) {
        return lazy(() -> _1(),
                    () -> _2(),
                    () -> _3(),
                    () -> _4(),
                    () -> _5(),
                    () -> _6(),
                    () -> fn.apply(_7()));
    }

    public <R> R fold(Function7<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? extends R> fn) {
        return fn.apply(_1(),
                        _2(),
                        _3(),
                        _4(),
                        _5(),
                        _6(),
                        _7());
    }

    @Override
    public String toString() {
        return String.format("[%s,%s,%s,%s,%s,%s,%s]",
                             _1(),
                             _2(),
                             _3(),
                             _4(),
                             _5(),
                             _6(),
                             _7());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Tuple7)) {
            return false;
        }
        Tuple7<?, ?, ?, ?, ?, ?, ?> tuple7 = (Tuple7<?, ?, ?, ?, ?, ?, ?>) o;
        return Objects.equals(_1(),
                              tuple7._1()) && Objects.equals(_2(),
                                                             tuple7._2()) && Objects.equals(_3(),
                                                                                            tuple7._3()) && Objects.equals(_4(),
                                                                                                                           tuple7._4())
            && Objects.equals(_5(),
                              tuple7._5()) && Objects.equals(_6(),
                                                             tuple7._6()) && Objects.equals(_7(),
                                                                                            tuple7._7());
    }

    @Override
    public int hashCode() {
        return Objects.hash(_1(),
                            _2(),
                            _3(),
                            _4(),
                            _5(),
                            _6(),
                            _7());
    }

    public final Object[] toArray() {
        return new Object[]{_1(), _2(), _3(), _4(), _5(), _6(), _7()};
    }

    public <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> concat(Tuple1<T8> tuple) {
        return Tuple.tuple(_1(),
                           _2(),
                           _3(),
                           _4(),
                           _5(),
                           _6(),
                           _7(),
                           tuple._1());
    }

    public <T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> lazyConcat(Tuple1<T8> tuple) {
        return Tuple.lazy(() -> _1(),
                          () -> _2,
                          () -> _3,
                          () -> _4,
                          () -> _5,
                          () -> _6,
                          () -> _7,
                          () -> tuple._1());
    }


}

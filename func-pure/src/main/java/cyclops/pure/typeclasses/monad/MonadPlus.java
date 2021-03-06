package cyclops.pure.typeclasses.monad;


import cyclops.function.higherkinded.Higher;
import cyclops.pure.arrow.MonoidK;
import cyclops.container.immutable.ImmutableList;

public interface MonadPlus<CRE> extends MonadZero<CRE> {

    <T> MonoidK<CRE> monoid();


    @Override
    default <T> Higher<CRE, T> zero() {
        return this.<T>monoid().zero();
    }


    default <T> Higher<CRE, T> plus(Higher<CRE, T> a,
                                    Higher<CRE, T> b) {
        return this.monoid()
                   .apply(a,
                          b);
    }


    default <T> Higher<CRE, T> sum(ImmutableList<Higher<CRE, T>> list) {
        return list.foldLeft(this.zero(),
                             this::plus);
    }


}

package com.oath.cyclops.anym.transformers;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import cyclops.container.immutable.impl.Vector;
import cyclops.monads.Witness.reactiveSeq;
import cyclops.reactor.container.transformer.SeqT;
import cyclops.reactor.container.transformer.VectorT;
import cyclops.reactive.ReactiveSeq;

/**
 * Represents a Traversable Monad Transformer, the monad transformer instance manipulates a nested non-scalar data type
 *
 * @author johnmcclean
 *
 * @param <T> Data type of the elements stored inside the traversable manipulated by this monad transformer
 */
@Deprecated
public interface TransformerTraversable<T>{


    default VectorT<reactiveSeq,T> groupedT(final int groupSize) {
        return VectorT.fromStream(stream().grouped(groupSize));
    }


    default SeqT<reactiveSeq,T> slidingT(final int windowSize, final int increment) {
        return SeqT.fromStream(stream().sliding(windowSize, increment));
    }


    default SeqT<reactiveSeq,T> slidingT(final int windowSize) {
        return SeqT.fromStream(stream().sliding(windowSize));
    }


    default VectorT<reactiveSeq,T> groupedUntilT(final Predicate<? super T> predicate) {
        return VectorT.fromStream(stream().groupedUntil(predicate));
    }


    default VectorT<reactiveSeq,T> groupedUntilT(final BiPredicate<Vector<? super T>, ? super T> predicate) {
        return VectorT.fromStream(stream().groupedUntil(predicate));
    }


    default VectorT<reactiveSeq,T> groupedWhileT(final Predicate<? super T> predicate) {
        return VectorT.fromStream(stream().groupedUntil(predicate));
    }

    public ReactiveSeq<T> stream();
}

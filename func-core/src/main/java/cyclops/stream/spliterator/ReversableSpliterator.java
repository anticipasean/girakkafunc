package cyclops.stream.spliterator;

public interface ReversableSpliterator<T> extends CopyableSpliterator<T> {

    boolean isReverse();

    void setReverse(boolean reverse);

    default ReversableSpliterator<T> invert() {
        setReverse(!isReverse());
        return this;
    }

    ReversableSpliterator<T> copy();


}

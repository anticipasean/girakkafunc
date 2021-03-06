package cyclops.pure.arrow;


import cyclops.function.higherkinded.Higher;
import cyclops.function.combiner.Group;

public interface GroupK<W> extends MonoidK<W> {


    <T> Higher<W, T> invert(Higher<W, T> wtHigher);

    default <T> Group<Higher<W, T>> asGroup() {
        return Group.of(a -> invert(a),
                        asMonoid());
    }


}

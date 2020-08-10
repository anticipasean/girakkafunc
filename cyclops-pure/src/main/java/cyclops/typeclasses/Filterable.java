package cyclops.typeclasses;

import cyclops.function.hkt.Higher;
import java.util.function.Predicate;

public interface Filterable<CRE> {

    <T> Higher<CRE, T> filter(Predicate<? super T> predicate,
                              Higher<CRE, T> ds);
}

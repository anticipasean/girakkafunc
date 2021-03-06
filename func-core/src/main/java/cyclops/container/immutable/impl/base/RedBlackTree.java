package cyclops.container.immutable.impl.base;


import static cyclops.pattern.Api.Case;

import cyclops.container.immutable.impl.base.redblacktree.Leaf;
import cyclops.container.immutable.impl.base.redblacktree.Tree;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.pattern.Api;
import java.io.Serializable;
import java.util.Comparator;
import java.util.stream.Stream;


public interface RedBlackTree extends Serializable {

    static <K, V> Tree<K, V> rootIsBlack(Tree<K, V> root) {
        return Api.MatchType(root)
                  .with(Case(node -> node.withBlack(true)),
                        Case(leaf -> leaf));
    }

    @SuppressWarnings("unchecked")
    static <K, V> Tree<K, V> fromStream(Comparator<? super K> comp,
                                        Stream<? extends Tuple2<? extends K, ? extends V>> stream) {
        Tree<K, V>[] tree = new Tree[1];
        tree[0] = new Leaf(comp);
        stream.forEach(t -> {
            tree[0] = tree[0].plus(t._1(),
                                   t._2());
        });
        return tree[0];
    }

    static <K, V> Tree<K, V> empty(Comparator<? super K> comp) {
        return new Leaf<K, V>(comp);
    }


}

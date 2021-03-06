package cyclops.monads.collections.persistent;


import com.oath.cyclops.anym.AnyMSeq;
import cyclops.pure.reactive.collections.immutable.VectorX;
import cyclops.pure.reactive.collections.mutable.ListX;
import cyclops.monads.AnyM;
import cyclops.monads.Witness.vectorX;
import cyclops.monads.collections.AbstractAnyMSeqOrderedDependentTest;

import static org.junit.Assert.assertThat;

public class VectorXTest extends AbstractAnyMSeqOrderedDependentTest<vectorX> {

	@Override
	public <T> AnyMSeq<vectorX,T> of(T... values) {
		return AnyM.fromVectorX(VectorX.of(values));
	}

	@Override
	public <T> AnyMSeq<vectorX,T> empty() {
		return AnyM.fromVectorX(VectorX.empty());
	}


}


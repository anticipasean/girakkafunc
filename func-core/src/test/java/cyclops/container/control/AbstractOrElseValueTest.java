package cyclops.container.control;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import cyclops.container.foldable.OrElseValue;
import org.junit.Test;

public abstract class AbstractOrElseValueTest {

    boolean lazy = true;

    public abstract OrElseValue<Integer, OrElseValue<Integer, ?>> of(int value);

    public abstract OrElseValue<Integer, OrElseValue<Integer, ?>> empty1();

    public abstract OrElseValue<Integer, OrElseValue<Integer, ?>> empty2();

    public abstract OrElseValue<Integer, OrElseValue<Integer, ?>> empty3();

    public abstract OrElseValue<Integer, OrElseValue<Integer, ?>> empty4();

    public abstract boolean isLazy();

    @Test
    public void recoverWith_switchesOnEmpty() {
        assertThat(empty1().recoverWith(() -> of(1)),
                   equalTo(of(1)));
    }

    @Test
    public void recoverWith_doesntswitchesWhenNotEmpty() {
        assertThat(of(1).recoverWith(() -> of(2)),
                   equalTo(of(1)));
    }

    @Test
    public void lazyRecoverWithTest1() {
        if (isLazy()) {
            empty1().recoverWith(() -> {
                lazy = false;
                return of(10);
            });

            assertTrue(lazy);
        }
    }

    @Test
    public void lazyRecoverWithTest2() {
        if (isLazy()) {
            empty2().recoverWith(() -> {
                lazy = false;
                return of(10);
            });

            assertTrue(lazy);
        }
    }

    @Test
    public void lazyRecoverWithTest3() {
        if (isLazy()) {
            empty3().recoverWith(() -> {
                lazy = false;
                return of(10);
            });

            assertTrue(lazy);
        }
    }

    @Test
    public void lazyRecoverWithTest4() {
        if (isLazy()) {
            empty4().recoverWith(() -> {
                lazy = false;
                return of(10);
            });

            assertTrue(lazy);
        }
    }
}

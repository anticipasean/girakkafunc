package cyclops.reactive.collection.container.mutable;

import com.google.common.collect.testing.QueueTestSuiteBuilder;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({QueueXTestSuite.QTest.class})
public class QueueXTestSuite {

    public static class QTest {

        public static Test suite() {
            return new QTest().allTests();
        }

        public TestSuite allTests() {
            TestSuite suite = new TestSuite("com.oath.cyclops.function.collections.extensions.guava");
            suite.addTest(testForOneToWayUseMySet());

            return suite;
        }

        public Test testForOneToWayUseMySet() {
            return QueueTestSuiteBuilder.using(new QueueXGenerator())
                                        .named("QueueX test")
                                        .withFeatures(CollectionSize.ANY,
                                                      CollectionFeature.ALLOWS_NULL_VALUES,
                                                      CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                                                      CollectionFeature.SUPPORTS_ADD,

                                                      CollectionFeature.SUPPORTS_REMOVE,
                                                      CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                                                      CollectionFeature.SUPPORTS_REMOVE)
                                        .createTestSuite();
        }
    }

}

package cyclops.stream.spliterator.push.filter;

import cyclops.stream.spliterator.push.FilterOperator;
import cyclops.stream.spliterator.push.SubscriberSource;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.Test;

@Test
public class FilterTckBlackBoxSubscriberTest extends SubscriberBlackboxVerification<Long> {

    public FilterTckBlackBoxSubscriberTest() {
        super(new TestEnvironment(300L));
    }

    @Override
    public Subscriber<Long> createSubscriber() {
        SubscriberSource<Long> sub = new SubscriberSource<Long>();

        new FilterOperator<Long>(sub,
                                 i -> true).subscribe(System.out::println,
                                                      System.err::println,
                                                      () -> {
                                                      });
        return sub;

    }

    @Override
    public Long createElement(int element) {
        return new Long(element);
    }


}

package io.github.anticipasean.ent.pattern;

import cyclops.container.control.Option;
import cyclops.container.immutable.impl.Seq;
import cyclops.container.immutable.tuple.Tuple2;
import cyclops.reactive.ReactiveSeq;
import io.github.anticipasean.ent.Ent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class KeyValueMatchingTest {


    @Test
    public void keyFitsConditionTest() {
        Number numberFive = Integer.valueOf(5);
        Tuple2<String, String> patternMatchingResult = Matcher.caseWhen("five",
                                                                        numberFive)
                                                              .valueOfTypeAndBothFit(Double.class,
                                                                                     (s, vDoubl) -> s.equalsIgnoreCase("one-half")
                                                                                         && vDoubl.compareTo(0.5D) == 0)
                                                              .then((s, aDouble) -> Tuple2.of(s,
                                                                                              "0.5"))
                                                              .valueOfTypeAndBothFit(BigDecimal.class,
                                                                                     (s, bigD) -> "five".equalsIgnoreCase(s)
                                                                                         && BigDecimal.valueOf(5.0000D)
                                                                                                      .equals(bigD))
                                                              .then(Function.identity(),
                                                                    bigDecimalForm -> "5.0000")
                                                              .valueOfTypeAndBothFit(Integer.class,
                                                                                     (s, i) -> "five".equalsIgnoreCase(s)
                                                                                         && 5 == i)
                                                              .then(Function.identity(),
                                                                    (integerForm) -> "5")
                                                              .elseDefault(Tuple2.of("No match",
                                                                                     "No match"));
        //        logger.info("pattern_matching_result: [ value expected: {} as {}, actual: {} as {} ]",
        //                    "5",
        //                    String.class,
        //                    patternMatchingResult._2(),
        //                    patternMatchingResult._2()
        //                                         .getClass());
        Assert.assertEquals("5",
                            patternMatchingResult._2());


    }

    @Test
    public void ifKeyValuePairFitsConditionTest() {
        Number numberFive = Integer.valueOf(5);
        Tuple2<String, String> patternMatchingResult = Matcher.caseWhen("five",
                                                                        numberFive)
                                                              .bothFit((s, number) -> "one-half".equals(s)
                                                                  && 0.5D == number.doubleValue())
                                                              .then((s, aDouble) -> Tuple2.of(s,
                                                                                              "0.5"))
                                                              .bothFit((s, number) -> "precise 5".equals(s)
                                                                  && BigDecimal.valueOf(5.000D)
                                                                               .equals(number))
                                                              .then((s, number) -> Tuple2.of(s,
                                                                                             BigDecimal.valueOf(5.0000D)
                                                                                                       .toEngineeringString()))
                                                              .bothFit((s, number) -> "five".equals(s) && 5 == number.intValue())
                                                              .then((s, number) -> Tuple2.of(s,
                                                                                             Integer.valueOf(5)
                                                                                                    .toString()))
                                                              .elseDefault(Tuple2.of("noMatch",
                                                                                     "noMatch"));
        //        logger.info("pattern_matching_result: [ expected: {} as {}, actual: {} as {} ]",
        //                    Tuple2.of("five",
        //                              "5"),
        //                    Tuple2.class,
        //                    patternMatchingResult,
        //                    patternMatchingResult.getClass());
        Assert.assertEquals(patternMatchingResult,
                            Tuple2.of("five",
                                      "5"));


    }

//    @Test
//    public void matchAndThenMapConcatTest() {
//
//        Seq<Tuple2<Integer, List<Integer>>> tuples = Seq.cons(Tuple2.<Integer, List<Integer>>of(12,
//                                                                                                Arrays.<Integer>asList(1,
//                                                                                                                       2,
//                                                                                                                       3)),
//                                                              Seq.empty())
//                                                        .append(Tuple2.<Integer, List<Integer>>of(13,
//                                                                                                  Arrays.<Integer>asList(4,
//                                                                                                                         5,
//                                                                                                                         6)));
//
//        Ent<Integer, Integer> ent = Ent.fromIterable(tuples)
//                                       .matchMap(matcher -> matcher.caseWhenKeyValue()
//                                                                   .keyFits(integer -> integer > 10)
//                                                                   .thenMapConcat((integer, integers) -> ReactiveSeq.fromIterable(integers)
//                                                                                                                    .zip(ReactiveSeq.of(integer)
//                                                                                                                                    .cycle(3)))
//                                                                   .elseNullable());
//        Assert.assertEquals(ent.size(),
//                            6,
//                            "six pairs of ints not generated: " + ent.mkString());
//    }


    @Test
    public void matchPutAllTest() {
        Seq<Tuple2<String, Number>> tuples = Seq.of("one",
                                                    "two_float")
                                                .zip(Seq.<Number>of(1,
                                                                    2.0F));
        Ent<String, Number> numEnt = Ent.fromIterable(tuples);

        Seq<Tuple2<String, Number>> tuples2 = Seq.of("big_int_one",
                                                     "sixty-three_plus",
                                                     "big_three_22")
                                                 .zip(Seq.<Number>of(BigInteger.valueOf(1231313112323L),
                                                                     63.2F,
                                                                     BigDecimal.valueOf(3.22)));
        Assert.assertEquals(numEnt.size(),
                            2);
        numEnt = numEnt.matchPutAll(tuples2,
                                    matcher -> matcher.caseWhenKeyValue()
                                                      .valueOfType(Integer.class)
                                                      .then((s, integer) -> Tuple2.of(String.valueOf(integer),
                                                                                      (Number) integer))
                                                      .valueOfType(Float.class)
                                                      .then((s, aFloat) -> Tuple2.of(String.valueOf(aFloat),
                                                                                     aFloat))
                                                      .valueOfType(BigDecimal.class)
                                                      .then((s, bigDecimal) -> Tuple2.of(String.valueOf(bigDecimal),
                                                                                         bigDecimal))
                                                      .elseDefault(null));
        Assert.assertEquals(numEnt.size(),
                            4); // minus the BigInteger which was not covered as a possible mapping and no default value provided for it
    }

    @Test
    public void optionClausePositiveMatchTest() {

        Object doubleOptionUpcast = Option.of(1231.122D);

        Tuple2<Integer, Number> integerNumberTuple2 = Matcher.of(Tuple2.of(25,
                                                                           doubleOptionUpcast))
                                                             .caseWhenKeyValue()
                                                             .valueOptionOfType(Integer.class)
                                                             .then(integerOptionTuple2 -> integerOptionTuple2.map2(opt -> (Number) opt.orElse(-12)))
                                                             .valueFits(o -> o.equals(Integer.valueOf(9234)))
                                                             .then(integerDoubleTuple2 -> integerDoubleTuple2.map2(aDouble -> (Number) aDouble))
                                                             .valueOptionOfType(Double.class)
                                                             .then(integerOptionTuple2 -> integerOptionTuple2.map2(doubles -> (Number) doubles.orElse(1.2D)))
                                                             .valueOptionOfType(BigDecimal.class)
                                                             .then((integer, bigDecimals) -> Tuple2.of(integer,
                                                                                                       (Number) BigDecimal.TEN))
                                                             .elseDefault(Tuple2.of(123,
                                                                                    78));
        Assert.assertEquals(integerNumberTuple2._2()
                                               .doubleValue(),
                            1231.122D);
    }

    @Test
    public void optionClauseNegativeMatchTest() {

        Object doubleOptionUpcast = Option.of(1231.122D);

        Tuple2<Integer, Number> integerNumberTuple2 = Matcher.of(Tuple2.of(25,
                                                                           doubleOptionUpcast))
                                                             .caseWhenKeyValue()
                                                             .valueOptionOfType(Integer.class)
                                                             .then(integerOptionTuple2 -> integerOptionTuple2.map2(opt -> (Number) opt.orElse(-12)))
                                                             .valueFits(o -> o.equals(Integer.valueOf(9234)))
                                                             .then(integerDoubleTuple2 -> integerDoubleTuple2.map2(aDouble -> (Number) aDouble))
                                                             .valueOptionOfType(Float.class)
                                                             .then(intFloatOptTuple -> intFloatOptTuple.map2(floatValOpt -> (Number) floatValOpt.orElse(1.2F)))
                                                             .valueOptionOfType(BigDecimal.class)
                                                             .then((integer, bigDecimals) -> Tuple2.of(integer,
                                                                                                       (Number) BigDecimal.TEN))
                                                             .elseDefault(Tuple2.of(123,
                                                                                    78));
        Assert.assertEquals(integerNumberTuple2._2()
                                               .doubleValue(),
                            78.0D,
                            "no match was expected for this pattern so the default was expected");
    }

    @Test
    public void optionClausePositiveMatchFirstClauseTest() {

        Object doubleOptionUpcast = Option.of(1231.122D);

        Tuple2<Integer, Number> integerNumberTuple2 = Matcher.of(Tuple2.of(25,
                                                                           doubleOptionUpcast))
                                                             .caseWhenKeyValue()
                                                             .valueOptionOfType(Double.class)
                                                             .then(integerOptionTuple2 -> integerOptionTuple2.map2(doubles -> (Number) doubles.orElse(1.2D)))
                                                             .valueOptionOfType(Integer.class)
                                                             .then(integerOptionTuple2 -> integerOptionTuple2.map2(opt -> (Number) opt.orElse(-12)))
                                                             .valueFits(o -> o.equals(Integer.valueOf(9234)))
                                                             .then(integerDoubleTuple2 -> integerDoubleTuple2.map2(aDouble -> (Number) aDouble))
                                                             .valueOptionOfType(BigDecimal.class)
                                                             .then((integer, bigDecimals) -> Tuple2.of(integer,
                                                                                                       (Number) BigDecimal.TEN))
                                                             .elseDefault(Tuple2.of(123,
                                                                                    78));
        Assert.assertEquals(integerNumberTuple2._2()
                                               .doubleValue(),
                            1231.122D);
    }

    @Test
    public void optionClauseNegativeMatchFirstClauseTest() {

        Object doubleOptionUpcast = Option.of(1231.122D);

        Tuple2<Integer, Number> integerNumberTuple2 = Matcher.of(Tuple2.of(25,
                                                                           doubleOptionUpcast))
                                                             .caseWhenKeyValue()
                                                             .valueOptionOfType(BigDecimal.class)
                                                             .then((integer, bigDecimals) -> Tuple2.of(integer,
                                                                                                       (Number) BigDecimal.TEN))
                                                             .valueFits(o -> o.equals(Integer.valueOf(22)))
                                                             .then((integer, o) -> Tuple2.of(integer,
                                                                                             (Number) 79))
                                                             .valueOptionOfType(Integer.class)
                                                             .then(integerOptionTuple2 -> integerOptionTuple2.map2(opt -> (Number) opt.orElse(-12)))
                                                             .valueFits(o -> o.equals(Integer.valueOf(9234)))
                                                             .then(integerDoubleTuple2 -> integerDoubleTuple2.map2(aDouble -> (Number) aDouble))
                                                             .valueOptionOfType(Float.class)
                                                             .then(intFloatOptTuple -> intFloatOptTuple.map2(floatValOpt -> (Number) floatValOpt.orElse(1.2F)))
                                                             .elseDefault(Tuple2.of(123,
                                                                                    78));
        Assert.assertEquals(integerNumberTuple2._2()
                                               .doubleValue(),
                            78.0D,
                            "no match was expected for this pattern so the default was expected");
    }
}

package funcify;

import static java.util.Arrays.asList;

import funcify.typedef.JavaAnnotation;
import funcify.typedef.JavaCodeBlock;
import funcify.typedef.JavaMethod;
import funcify.typedef.JavaModifier;
import funcify.typedef.JavaParameter;
import funcify.typedef.JavaTypeDefinition;
import funcify.typedef.JavaTypeKind;
import funcify.typedef.javastatement.ReturnStatement;
import funcify.typedef.javastatement.TemplatedExpression;
import funcify.typedef.javatype.JavaType;
import funcify.typedef.javatype.SimpleJavaTypeVariable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author smccarron
 * @created 2021-05-22
 */
public class EnsembleInterfaceTypeAssembler {

    public static final String FUNCIFY_ENSEMBLE_PACKAGE_NAME = "funcify.ensemble";
    public static final SimpleJavaTypeVariable WITNESS_TYPE_VARIABLE = SimpleJavaTypeVariable.of("WT");

    public EnsembleInterfaceTypeAssembler() {

    }

    public GenerationSession assembleEnsembleInterfaceTypes(final GenerationSession generationSession) {
        final JavaTypeDefinition baseEnsembleInterfaceTypeDefinition = baseEnsembleInterfaceTypeCreator(JavaTypeDefinitionFactory.getInstance());
        return generationSession.withBaseEnsembleInterfaceTypeDefinition(baseEnsembleInterfaceTypeDefinition)
                                .withEnsembleInterfaceTypeDefinitionsByEnsembleKind(generationSession.getEnsembleKinds()
                                                                                                     .stream()
                                                                                                     .map(ek -> new SimpleImmutableEntry<>(ek,
                                                                                                                                           buildEnsembleInterfaceTypeDefinitionForEnsembleKind(JavaTypeDefinitionFactory.getInstance(),
                                                                                                                                                                                               baseEnsembleInterfaceTypeDefinition,
                                                                                                                                                                                               ek)))
                                                                                                     .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                                               Map.Entry::getValue)));

    }

    private static <D extends Definition<D>> D buildEnsembleInterfaceTypeDefinitionForEnsembleKind(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                                   final JavaTypeDefinition baseEnsembleInterfaceTypeDefinition,
                                                                                                   final EnsembleKind ensembleKind) {
        final JavaType ensembleInterfaceSuperType = ensembleKindInterfaceTypeSuperTypeCreator(javaDefinitionFactory,
                                                                                              baseEnsembleInterfaceTypeDefinition,
                                                                                              ensembleKind);
        return javaDefinitionFactory.name(ensembleKind.getSimpleClassName())
                                    .foldUpdate(javaDefinitionFactory::javaPackage,
                                                FUNCIFY_ENSEMBLE_PACKAGE_NAME)
                                    .foldUpdate(javaDefinitionFactory::typeVariables,
                                                Stream.concat(Stream.of(WITNESS_TYPE_VARIABLE),
                                                              firstNSimpleJavaTypeVariables(ensembleKind.getNumberOfValueParameters()))
                                                      .collect(Collectors.toList()))
                                    .foldUpdate(javaDefinitionFactory::modifier,
                                                JavaModifier.PUBLIC)
                                    .foldUpdate(javaDefinitionFactory::typeKind,
                                                JavaTypeKind.INTERFACE)
                                    .foldUpdate(javaDefinitionFactory::superType,
                                                ensembleInterfaceSuperType)
                                    .update(createAndUpdateWithConvertMethod(javaDefinitionFactory,
                                                                             ensembleKind,
                                                                             ensembleInterfaceSuperType))
                                    .update(createAndUpdateWithNarrowMethodIfSolo(javaDefinitionFactory,
                                                                                  ensembleKind));

    }

    private static <D extends Definition<D>> Function<D, D> createAndUpdateWithNarrowMethodIfSolo(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                                  final EnsembleKind ensembleKind) {
        return (D definition) -> {
            if (ensembleKind != EnsembleKind.SOLO) {
                return definition;
            }
            final JavaType returnTypeBaseVariable = javaDefinitionFactory.simpleJavaTypeVariable("S");
            final JavaType lowerBoundWildcardValueTypeParameter = javaDefinitionFactory.javaTypeVariableWithWildcardLowerBounds(firstNSimpleJavaTypeVariables(ensembleKind.getNumberOfValueParameters()).findFirst()
                                                                                                                                                                                                        .orElseThrow(IllegalStateException::new));
            final JavaType returnTypeBaseVariableSuperType = javaDefinitionFactory.parameterizedJavaType(FUNCIFY_ENSEMBLE_PACKAGE_NAME,
                                                                                                         ensembleKind.getSimpleClassName(),
                                                                                                         WITNESS_TYPE_VARIABLE,
                                                                                                         lowerBoundWildcardValueTypeParameter);
            final JavaType returnTypeVariable = javaDefinitionFactory.javaTypeVariableWithUpperBounds(returnTypeBaseVariable,
                                                                                                      returnTypeBaseVariableSuperType);

            final JavaDefinitionFactory<JavaMethod> javaMethodDefinitionFactory = JavaMethodFactory.getInstance();
            return javaDefinitionFactory.method(definition,
                                                javaMethodDefinitionFactory.name("narrow")
                                                                           .foldUpdate(javaMethodDefinitionFactory::javaAnnotation,
                                                                                       JavaAnnotation.builder()
                                                                                                     .name("SuppressWarnings")
                                                                                                     .parameters(fromPairs("value",
                                                                                                                           "unchecked"))
                                                                                                     .build())
                                                                           .foldUpdate(javaMethodDefinitionFactory::modifier,
                                                                                       JavaModifier.DEFAULT)
                                                                           .foldUpdate(javaMethodDefinitionFactory::typeVariable,
                                                                                       returnTypeVariable)
                                                                           .foldUpdate(javaMethodDefinitionFactory::returnType,
                                                                                       returnTypeBaseVariable)
                                                                           .foldUpdate(javaMethodDefinitionFactory::codeBlock,
                                                                                       JavaCodeBlockFactory.getInstance()
                                                                                                           .statement(JavaCodeBlockFactory.getInstance()
                                                                                                                                          .name(""),
                                                                                                                      ReturnStatement.builder()
                                                                                                                                     .expressions(asList(TemplatedExpression.builder()
                                                                                                                                                                            .templateCall("cast_as")
                                                                                                                                                                            .templateParameters(asList("this",
                                                                                                                                                                                                       "S"))
                                                                                                                                                                            .build()))
                                                                                                                                     .build())));
        };
    }

    private static <D extends Definition<D>> Function<D, D> createAndUpdateWithConvertMethod(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                             final EnsembleKind ensembleKind,
                                                                                             final JavaType ensembleInterfaceSuperType) {
        return (D definition) -> {
            return createConverterMethodForEnsembleInterfaceType(javaDefinitionFactory,
                                                                 definition,
                                                                 ensembleKind,
                                                                 ensembleInterfaceSuperType);
        };
    }

    private static <D extends Definition<D>> D createConverterMethodForEnsembleInterfaceType(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                             final D definition,
                                                                                             final EnsembleKind ensembleKind,
                                                                                             final JavaType ensembleInterfaceSuperType) {
        final JavaType returnTypeVariable = firstNSimpleJavaTypeVariables(
            ensembleKind.getNumberOfValueParameters() + 1).collect(Collectors.toList())
                                                          .get(ensembleKind.getNumberOfValueParameters());
        final JavaDefinitionFactory<JavaMethod> javaMethodDefinitionFactory = JavaMethodFactory.getInstance();
        return javaDefinitionFactory.method(javaDefinitionFactory.javaImport(definition,
                                                                             Function.class,
                                                                             Objects.class),
                                            javaMethodDefinitionFactory.name("convert")
                                                                       .foldUpdate(javaMethodDefinitionFactory::modifier,
                                                                                   JavaModifier.DEFAULT)
                                                                       .foldUpdate(javaMethodDefinitionFactory::typeVariable,
                                                                                   returnTypeVariable)
                                                                       .foldUpdate(javaMethodDefinitionFactory::returnType,
                                                                                   returnTypeVariable)
                                                                       .foldUpdate(javaMethodDefinitionFactory::parameter,
                                                                                   converterFunctionParameter(javaMethodDefinitionFactory,
                                                                                                              ensembleKind,
                                                                                                              ensembleInterfaceSuperType,
                                                                                                              returnTypeVariable))
                                                                       .foldUpdate(javaMethodDefinitionFactory::codeBlock,
                                                                                   converterMethodCodeBlock(JavaCodeBlockFactory.getInstance())));
    }

    private static <D extends Definition<D>> JavaType ensembleKindInterfaceTypeSuperTypeCreator(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                                final JavaTypeDefinition baseEnsembleInterfaceTypeDefinition,
                                                                                                final EnsembleKind ensembleKind) {
        if (ensembleKind == EnsembleKind.SOLO) {
            return baseEnsembleInterfaceTypeDefinition.getJavaType();
        } else {
            return firstNSimpleJavaTypeVariables(ensembleKind.getNumberOfValueParameters()).skip(1)
                                                                                           .reduce(soloEnsembleInterfaceTypeCreator(javaDefinitionFactory),
                                                                                                   nestingSoloTypeVariableCreator(javaDefinitionFactory));
        }
    }

    private static <D extends Definition<D>> D baseEnsembleInterfaceTypeCreator(final JavaDefinitionFactory<D> javaDefinitionFactory) {
        return javaDefinitionFactory.name("Ensemble")
                                    .foldUpdate(javaDefinitionFactory::javaPackage,
                                                FUNCIFY_ENSEMBLE_PACKAGE_NAME)
                                    .foldUpdate(javaDefinitionFactory::modifier,
                                                JavaModifier.PUBLIC)
                                    .foldUpdate(javaDefinitionFactory::typeKind,
                                                JavaTypeKind.INTERFACE)
                                    .foldUpdate(javaDefinitionFactory::typeVariable,
                                                WITNESS_TYPE_VARIABLE);
    }

    //TODO: Expand methods within code block def factory to streamline the creation of these expressions
    private static JavaCodeBlock converterMethodCodeBlock(JavaDefinitionFactory<JavaCodeBlock> javaCodeBlockDefinitionFactory) {
        return javaCodeBlockDefinitionFactory.name("")
                                             .foldUpdate(javaCodeBlockDefinitionFactory::statement,
                                                         ReturnStatement.builder()
                                                                        .expressions(asList(TemplatedExpression.builder()
                                                                                                               .templateCall("function_call")
                                                                                                               .templateParameters(asList("converter",
                                                                                                                                          "this"))
                                                                                                               .build()))
                                                                        .build());
    }

    private static <K, V> Map<K, V> fromPairs(final K k,
                                              final V v) {
        return Stream.of(new SimpleImmutableEntry<>(k,
                                                    v))
                     .collect(Collectors.toMap(Entry::getKey,
                                               Entry::getValue));
    }

    private static <D extends Definition<D>> JavaParameter converterFunctionParameter(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                      final EnsembleKind ensembleKind,
                                                                                      final JavaType ensembleInterfaceSuperType,
                                                                                      final JavaType returnTypeVariable) {
        return JavaParameter.builder()
                            .name("converter")
                            .modifiers(asList(JavaModifier.FINAL))
                            .type(converterFunctionParameterType(javaDefinitionFactory,
                                                                 ensembleKind,
                                                                 ensembleInterfaceSuperType,
                                                                 returnTypeVariable))
                            .build();
    }

    private static <D extends Definition<D>> JavaType converterFunctionParameterType(final JavaDefinitionFactory<D> javaDefinitionFactory,
                                                                                     final EnsembleKind ensembleKind,
                                                                                     final JavaType ensembleInterfaceSuperType,
                                                                                     final JavaType returnTypeVariable) {
        if (ensembleKind == EnsembleKind.SOLO) {
            return javaDefinitionFactory.covariantParameterizedFunctionJavaType(Function.class,
                                                                                soloEnsembleInterfaceTypeCreator(javaDefinitionFactory),
                                                                                returnTypeVariable);
        } else {
            return javaDefinitionFactory.covariantParameterizedFunctionJavaType(Function.class,
                                                                                ensembleInterfaceSuperType,
                                                                                returnTypeVariable);
        }
    }

    private static <D extends Definition<D>> JavaType soloEnsembleInterfaceTypeCreator(final JavaDefinitionFactory<D> javaDefinitionFactory) {
        return javaDefinitionFactory.parameterizedJavaType(FUNCIFY_ENSEMBLE_PACKAGE_NAME,
                                                           EnsembleKind.SOLO.getSimpleClassName(),
                                                           Stream.concat(Stream.of(WITNESS_TYPE_VARIABLE),
                                                                         firstNSimpleJavaTypeVariables(1))
                                                                 .collect(Collectors.toList()));
    }

    private static <D extends Definition<D>> BinaryOperator<JavaType> nestingSoloTypeVariableCreator(final JavaDefinitionFactory<D> javaDefinitionFactory) {
        return (jt1, jt2) -> javaDefinitionFactory.parameterizedJavaType(FUNCIFY_ENSEMBLE_PACKAGE_NAME,
                                                                         jt1.getName(),
                                                                         jt1,
                                                                         jt2);
    }

    private static Stream<JavaType> firstNSimpleJavaTypeVariables(int n) {
        return firstNLetters(n).map(SimpleJavaTypeVariable::of);
    }

    private static Stream<String> firstNLetters(int n) {
        return rangeOfCharactersFrom('A',
                                     (char) (((int) 'A') + Math.min(26,
                                                                    n)));
    }

    private static Stream<String> rangeOfCharactersFrom(char start,
                                                        char end) {
        if (start == end) {
            return Stream.of(String.valueOf(start));
        }
        if ((start - end) > 0) {
            return Stream.empty();
        } else {
            return Stream.iterate(start,
                                  (Character c) -> {
                                      return (char) (((int) c) + 1);
                                  })
                         .limit(end - start)
                         .map(String::valueOf);
        }
    }

}

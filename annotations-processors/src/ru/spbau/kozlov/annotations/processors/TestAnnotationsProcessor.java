package ru.spbau.kozlov.annotations.processors;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.annotations.Test;
import ru.spbau.kozlov.annotations.TestClass;
import ru.spbau.kozlov.annotations.annotators.AbstractMethodAnnotator;
import ru.spbau.kozlov.annotations.annotators.TestClassAnnotator;
import ru.spbau.kozlov.annotations.annotators.TestMethodAnnotator;
import ru.spbau.kozlov.annotations.generators.TestClassesGenerator;
import ru.spbau.kozlov.annotations.generators.TestUnitsGenerator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author adkozlov
 */
public class TestAnnotationsProcessor extends AbstractAnnotationsProcessor {

    @NotNull
    private final Map<Name, TestClassAnnotator> testClassAnnotators = new HashMap<>();
    @NotNull
    private final Map<Name, List<TestMethodAnnotator>> testMethodAnnotators = new HashMap<>();
    @NotNull
    private final Map<String, List<String>> testUnits = new HashMap<>();

    @Override
    public boolean process(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnvironment) {
        processTestClassAnnotations(roundEnvironment);
        processTestAnnotations(roundEnvironment);

        for (Map.Entry<Name, TestClassAnnotator> entry : testClassAnnotators.entrySet()) {
            try {
                new TestClassesGenerator(getElements(),
                        entry.getValue().getElement(),
                        testMethodAnnotators.get(entry.getKey()).stream().map(AbstractMethodAnnotator::getElement))
                        .generate(getFiler());
            } catch (IOException e) {
                handleIOException(e);
            }
        }

        for (Map.Entry<String, List<String>> entry : testUnits.entrySet()) {
            try {
                new TestUnitsGenerator(entry.getKey(), entry.getValue()).generate(getFiler());
            } catch (IOException e) {
                handleIOException(e);
            }
        }

        return true;
    }

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Arrays.stream(new Class<?>[]{TestClass.class, Test.class})
                .map(Class::getCanonicalName)
                .collect(Collectors.toSet());
    }

    private void processTestClassAnnotations(@NotNull RoundEnvironment roundEnvironment) {
        processAnnotations(roundEnvironment, TestClass.class, element -> {
            if (element.getKind() != ElementKind.CLASS) {
                throw TestClassAnnotator.createException("Only a", true, TestClass.class);
            }

            TypeElement typeElement = (TypeElement) element;
            Name className = typeElement.getQualifiedName();
            testClassAnnotators.put(className, new TestClassAnnotator(typeElement));

            String testUnitName = typeElement.getAnnotation(TestClass.class).testUnit();
            testUnits.putIfAbsent(testUnitName, new ArrayList<>());
            testUnits.get(testUnitName).add(TestClassesGenerator.getTestClassName(className));
        });
    }

    private void processTestAnnotations(@NotNull RoundEnvironment roundEnvironment) {
        processAnnotations(roundEnvironment, Test.class, element -> {
            Name className = getMethodEnclosingElement(element, Test.class).getQualifiedName();
            if (testClassAnnotators.containsKey(className)) {
                addToMapOfLists(testMethodAnnotators, className, new TestMethodAnnotator((ExecutableElement) element));
            } else {
                printErrorMessage(element,
                        "Class \'%s\' is not annotated with \'%s\' annotation",
                        className,
                        TestClass.class.getName());
            }
        });
    }
}

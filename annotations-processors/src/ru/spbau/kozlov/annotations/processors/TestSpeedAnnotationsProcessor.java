package ru.spbau.kozlov.annotations.processors;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.annotations.TestSpeed;
import ru.spbau.kozlov.annotations.annotators.AbstractMethodAnnotator;
import ru.spbau.kozlov.annotations.annotators.TestSpeedMethodAnnotator;
import ru.spbau.kozlov.annotations.generators.InheritorsGenerator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.*;

/**
 * @author adkozlov
 */
public class TestSpeedAnnotationsProcessor extends AbstractAnnotationsProcessor {

    @NotNull
    private final Map<TypeElement, List<TestSpeedMethodAnnotator>> testSpeedMethodAnnotators = new HashMap<>();

    @Override
    public boolean process(@NotNull Set<? extends TypeElement> annotations, @NotNull RoundEnvironment roundEnvironment) {
        processTestSpeedAnnotations(roundEnvironment);

        for (Map.Entry<TypeElement, List<TestSpeedMethodAnnotator>> entry : testSpeedMethodAnnotators.entrySet()) {
            try {
                new InheritorsGenerator(getElements(),
                        entry.getKey(),
                        entry.getValue().stream().map(AbstractMethodAnnotator::getElement))
                        .generate(getFiler());
            } catch (IOException e) {
                handleIOException(e);
            }
        }

        return true;
    }

    @NotNull
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Collections.singletonList(TestSpeed.class.getName()));
    }

    private void processTestSpeedAnnotations(@NotNull RoundEnvironment roundEnvironment) {
        processAnnotations(roundEnvironment, TestSpeed.class, element -> addToMapOfLists(testSpeedMethodAnnotators,
                getMethodEnclosingElement(element, TestSpeed.class),
                new TestSpeedMethodAnnotator((ExecutableElement) element)));
    }
}

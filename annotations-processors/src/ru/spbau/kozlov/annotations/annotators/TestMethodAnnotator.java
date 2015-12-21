package ru.spbau.kozlov.annotations.annotators;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.annotations.Test;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author adkozlov
 */
public class TestMethodAnnotator extends AbstractMethodAnnotator {

    public TestMethodAnnotator(@NotNull ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    protected void checkElement() {
        super.checkElement();
        ExecutableElement executableElement = getElement();

        Set<Modifier> modifiers = executableElement.getModifiers();
        if (!modifiers.contains(Modifier.PUBLIC)) {
            throw createException("Only a public", true);
        }
        if (!modifiers.contains(Modifier.STATIC)) {
            throw createException("Only a static", true);
        }

        if (!executableElement.getTypeParameters().isEmpty()) {
            throw createException("Only parameterless", true);
        }
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return Test.class;
    }
}

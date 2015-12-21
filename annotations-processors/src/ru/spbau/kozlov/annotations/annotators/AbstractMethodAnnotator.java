package ru.spbau.kozlov.annotations.annotators;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;

/**
 * @author adkozlov
 */
public abstract class AbstractMethodAnnotator extends AbstractElementAnnotator {

    public AbstractMethodAnnotator(@NotNull ExecutableElement executableElement) {
        super(executableElement);
        checkElement();
    }

    @NotNull
    @Override
    public ExecutableElement getElement() {
        return (ExecutableElement) element;
    }

    @Override
    protected void checkElement() {
        if (getElement().getModifiers().contains(Modifier.ABSTRACT)) {
            throw createException("An abstract", false);
        }
    }

    @NotNull
    protected IllegalArgumentException createException(@NotNull String prefix, boolean can) {
        return createException(prefix, can, getAnnotation());
    }

    @NotNull
    public static IllegalArgumentException createException(@NotNull String prefix,
                                                           boolean can,
                                                           @NotNull Class<? extends Annotation> annotation) {
        return createException(prefix, ElementKind.METHOD, can, annotation);
    }
}

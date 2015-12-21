package ru.spbau.kozlov.annotations.annotators;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.annotations.TestClass;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author adkozlov
 */
public class TestClassAnnotator extends AbstractElementAnnotator {

    public TestClassAnnotator(@NotNull TypeElement typeElement) {
        super(typeElement);
    }

    @NotNull
    @Override
    public TypeElement getElement() {
        return (TypeElement) element;
    }

    @Override
    protected void checkElement() {
        Set<Modifier> modifiers = getElement().getModifiers();
        if (modifiers.contains(Modifier.PRIVATE)) {
            throw createException("A private", false);
        }
        if (modifiers.contains(Modifier.ABSTRACT)) {
            throw createException("An abstract", false);
        }
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return TestClass.class;
    }

    @NotNull
    protected IllegalArgumentException createException(@NotNull String prefix, boolean can) {
        return createException(prefix, can, getAnnotation());
    }

    @NotNull
    public static IllegalArgumentException createException(@NotNull String prefix,
                                                           boolean can,
                                                           @NotNull Class<? extends Annotation> annotation) {
        return createException(prefix, ElementKind.CLASS, can, annotation);
    }
}

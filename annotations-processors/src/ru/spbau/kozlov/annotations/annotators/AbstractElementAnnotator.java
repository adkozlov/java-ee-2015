package ru.spbau.kozlov.annotations.annotators;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import java.lang.annotation.Annotation;

/**
 * @author adkozlov
 */
public abstract class AbstractElementAnnotator {

    @NotNull
    protected final Element element;

    protected AbstractElementAnnotator(@NotNull Element element) {
        this.element = element;
        checkElement();
    }

    @NotNull
    public Element getElement() {
        return element;
    }

    protected abstract void checkElement();

    @NotNull
    protected abstract Class<? extends Annotation> getAnnotation();

    @NotNull
    protected static IllegalArgumentException createException(@NotNull String prefix,
                                                           @NotNull ElementKind elementKind,
                                                           boolean can,
                                                           @NotNull Class<? extends Annotation> annotation) {
        return new IllegalArgumentException(String.format("%s %s can%s be annotated with the \'%s\' annotation",
                prefix,
                getElementType(elementKind),
                can ? "" : "not",
                annotation.getName()));
    }

    @NotNull
    private static String getElementType(@NotNull ElementKind elementKind) {
        switch (elementKind) {
            case CLASS:
                return "class";
            case METHOD:
                return "method";
            default:
                throw new RuntimeException("Unsupported kind of element: " + elementKind);
        }
    }
}

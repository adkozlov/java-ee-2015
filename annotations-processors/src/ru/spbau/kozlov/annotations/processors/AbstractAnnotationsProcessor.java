package ru.spbau.kozlov.annotations.processors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.kozlov.annotations.annotators.AbstractMethodAnnotator;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author adkozlov
 */
public abstract class AbstractAnnotationsProcessor extends AbstractProcessor {

    @NotNull
    private Messager messager;
    @NotNull
    private Filer filer;
    @NotNull
    private Elements elements;

    @Override
    public synchronized void init(@NotNull ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();
        elements = processingEnvironment.getElementUtils();
    }

    @NotNull
    protected Filer getFiler() {
        return filer;
    }

    @NotNull
    protected Elements getElements() {
        return elements;
    }

    @NotNull
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    protected void processAnnotations(@NotNull RoundEnvironment roundEnvironment,
                                      @NotNull Class<? extends Annotation> annotationClass,
                                      @NotNull Consumer<Element> consumer) {
        roundEnvironment.getElementsAnnotatedWith(annotationClass).stream().forEach(element -> {
            try {
                consumer.accept(element);
            } catch (IllegalArgumentException e) {
                printErrorMessage(element, e.getMessage());
            }
        });
    }

    protected void printErrorMessage(@Nullable Element element,
                                     @NotNull String messageFormat,
                                     @NotNull Object... arguments) {
        printErrorMessage(element, String.format(messageFormat, arguments));
    }

    protected void printErrorMessage(@Nullable Element element, @NotNull String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    protected void handleIOException(@NotNull IOException e) {
        printErrorMessage(null, "I/O error occurred: %s", e.getMessage());
    }

    @NotNull
    protected static TypeElement getMethodEnclosingElement(@NotNull Element element,
                                                           @NotNull Class<? extends Annotation> annotation) {
        if (element.getKind() != ElementKind.METHOD) {
            throw AbstractMethodAnnotator.createException("Only a", true, annotation);
        }

        return (TypeElement) element.getEnclosingElement();
    }

    protected static <K, V> void addToMapOfLists(@NotNull Map<K, List<V>> map, @NotNull K key, @NotNull V value) {
        map.putIfAbsent(key, new ArrayList<>());
        map.get(key).add(value);
    }
}

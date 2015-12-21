package ru.spbau.kozlov.annotations.annotators;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.annotations.TestSpeed;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author adkozlov
 */
public class TestSpeedMethodAnnotator extends AbstractMethodAnnotator {

    public TestSpeedMethodAnnotator(@NotNull ExecutableElement executableElement) {
        super(executableElement);
    }

    @Override
    protected void checkElement() {
        super.checkElement();

        ExecutableElement executableElement = getElement();
        Set<Modifier> modifiers = executableElement.getModifiers();
        if (modifiers.contains(Modifier.STATIC)) {
            throw createException("A static", false);
        }
        if (modifiers.contains(Modifier.FINAL) || modifiers.contains(Modifier.PRIVATE)) {
            throw createException("An uninheritable", false);
        }

        new EnclosingTypeClassAnnotator((TypeElement) executableElement.getEnclosingElement()).checkElement();
    }

    @NotNull
    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return TestSpeed.class;
    }

    private class EnclosingTypeClassAnnotator extends TestClassAnnotator {

        public EnclosingTypeClassAnnotator(@NotNull TypeElement typeElement) {
            super(typeElement);
        }

        @Override
        protected void checkElement() {
            TypeElement typeElement = getElement();
            Set<Modifier> modifiers = typeElement.getModifiers();
            if (modifiers.contains(Modifier.PRIVATE)) {
                throw createException("a private");
            }
            if (modifiers.contains(Modifier.ABSTRACT)) {
                throw createException("an abstract");
            }
            if (modifiers.contains(Modifier.FINAL) || ElementFilter.constructorsIn(typeElement.getEnclosedElements())
                    .stream()
                    .allMatch(constructorElement -> constructorElement.getModifiers().contains(Modifier.PRIVATE))) {
                throw createException("an uninheritable");
            }
        }

        @NotNull
        @Override
        protected Class<? extends Annotation> getAnnotation() {
            return TestSpeedMethodAnnotator.this.getAnnotation();
        }

        @NotNull
        private IllegalArgumentException createException(@NotNull String suffix) {
            return createException("A method of " + suffix, false, getAnnotation());
        }
    }
}

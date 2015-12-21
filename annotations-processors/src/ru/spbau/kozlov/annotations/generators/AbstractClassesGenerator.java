package ru.spbau.kozlov.annotations.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author adkozlov
 */
public abstract class AbstractClassesGenerator implements IClassesGenerator {

    @NotNull
    private final TypeElement typeElement;
    @NotNull
    private final PackageElement packageElement;

    public AbstractClassesGenerator(@NotNull Elements elements,
                                    @NotNull TypeElement typeElement) {
        this.typeElement = typeElement;
        packageElement = elements.getPackageOf(typeElement);
    }

    @NotNull
    @Override
    public String getSimpleClassName() {
        return appendSuffix(typeElement.getSimpleName());
    }

    @NotNull
    @Override
    public String getClassName() {
        return appendSuffix(typeElement.getQualifiedName());
    }

    @Nullable
    @Override
    public String getPackageName() {
        return !packageElement.isUnnamed() ? packageElement.getQualifiedName().toString() : null;
    }

    @NotNull
    protected abstract String appendSuffix(@NotNull Name name);

    @NotNull
    protected TypeElement getTypeElement() {
        return typeElement;
    }
}

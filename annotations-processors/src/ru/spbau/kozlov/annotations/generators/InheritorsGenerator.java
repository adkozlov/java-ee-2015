package ru.spbau.kozlov.annotations.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author adkozlov
 */
public class InheritorsGenerator extends AbstractClassesGenerator {

    @NotNull
    public static final String INHERITOR_SUFFIX = "_TestSpeed";

    @NotNull
    private final Random random = new Random();
    @NotNull
    private final List<ExecutableElement> executableElements;
    @NotNull
    private final List<ExecutableElement> constructorElements;

    public InheritorsGenerator(@NotNull Elements elements,
                               @NotNull TypeElement typeElement,
                               @NotNull Stream<ExecutableElement> executableElements) {
        super(elements, typeElement);
        this.executableElements = executableElements.collect(Collectors.toList());
        constructorElements = ElementFilter.constructorsIn(typeElement.getEnclosedElements());
    }

    @Override
    public void printMethods(@NotNull IndentedWriter indentedWriter) throws IOException {
        for (ExecutableElement constructorElement : constructorElements) {
            if (!constructorElement.getModifiers().contains(Modifier.PRIVATE)) {
                printConstructorElement(indentedWriter, constructorElement);
                indentedWriter.println();
            }
        }

        for (Iterator<ExecutableElement> iterator = executableElements.iterator(); iterator.hasNext(); ) {
            printExecutableElement(indentedWriter, iterator.next());
            if (iterator.hasNext()) {
                indentedWriter.println();
            }
        }
    }

    private void printConstructorElement(@NotNull IndentedWriter indentedWriter,
                                         @NotNull ExecutableElement constructorElement) throws IOException {
        printModifiers(indentedWriter, constructorElement);
        indentedWriter.append(appendSuffix(getTypeElement().getSimpleName()));
        printParameters(indentedWriter, constructorElement, false);
        printThrownTypes(indentedWriter, constructorElement);
        indentedWriter.printlnLeftBrace();
        indentedWriter.print("super");
        printParameters(indentedWriter, constructorElement, true);
        indentedWriter.append(";");
        indentedWriter.println();
        indentedWriter.printlnRightBrace();
    }

    private void printExecutableElement(@NotNull IndentedWriter indentedWriter,
                                        @NotNull ExecutableElement executableElement) throws IOException {
        indentedWriter.println("@Override");
        printModifiers(indentedWriter, executableElement);
        TypeMirror returnType = executableElement.getReturnType();
        indentedWriter.append(returnType.toString());
        indentedWriter.append(" ");
        String methodName = executableElement.getSimpleName().toString();
        indentedWriter.append(methodName);
        printParameters(indentedWriter, executableElement, false);
        printThrownTypes(indentedWriter, executableElement);
        indentedWriter.printlnLeftBrace();
        String timeStampSuffix = Long.toHexString(random.nextLong());
        indentedWriter.printf("long timeStamp_%s = System.currentTimeMillis();", timeStampSuffix);
        indentedWriter.println();
        indentedWriter.print("try");
        indentedWriter.printlnLeftBrace();
        indentedWriter.print(returnType.getKind() != TypeKind.VOID ? "return " : "");
        indentedWriter.append("super.");
        indentedWriter.append(methodName);
        printParameters(indentedWriter, executableElement, true);
        indentedWriter.append(";");
        indentedWriter.println();
        indentedWriter.printRightBrace();
        indentedWriter.append(" finally ");
        indentedWriter.printlnLeftBrace();
        indentedWriter.printf("System.out.println(\"%s.%s \" + (System.currentTimeMillis() - timeStamp_%s));",
                getTypeElement().getQualifiedName(),
                methodName,
                timeStampSuffix);
        indentedWriter.println();
        indentedWriter.printlnRightBrace();
        indentedWriter.printlnRightBrace();
    }

    private static void printModifiers(@NotNull IndentedWriter indentedWriter,
                                       @NotNull ExecutableElement executableElement) throws IOException {
        indentedWriter.print("");
        for (Modifier modifier : executableElement.getModifiers()) {
            indentedWriter.append(modifier.toString());
            indentedWriter.append(" ");
        }
    }

    private static void printParameters(@NotNull IndentedWriter indentedWriter,
                                        @NotNull ExecutableElement executableElement,
                                        boolean onlyNames) throws IOException {
        indentedWriter.append("(");
        for (Iterator<? extends VariableElement> parametersIterator = executableElement.getParameters().iterator();
             parametersIterator.hasNext(); ) {
            VariableElement variableElement = parametersIterator.next();
            if (!onlyNames) {
                indentedWriter.append(variableElement.asType().toString());
                indentedWriter.append(" ");
            }

            indentedWriter.append(variableElement.getSimpleName().toString());
            if (parametersIterator.hasNext()) {
                indentedWriter.append(", ");
            }
        }
        indentedWriter.append(")");
    }

    private static void printThrownTypes(@NotNull IndentedWriter indentedWriter,
                                         @NotNull ExecutableElement executableElement) throws IOException {
        Iterator<? extends TypeMirror> thrownTypesIterator = executableElement.getThrownTypes().iterator();
        if (thrownTypesIterator.hasNext()) {
            indentedWriter.append(" throws ");
        }

        while (thrownTypesIterator.hasNext()) {
            indentedWriter.append(thrownTypesIterator.next().toString());
            if (thrownTypesIterator.hasNext()) {
                indentedWriter.append(", ");
            }
        }
    }

    @Nullable
    @Override
    public String getBaseClassName() {
        return getTypeElement().getQualifiedName().toString();
    }

    @NotNull
    @Override
    protected String appendSuffix(@NotNull Name name) {
        return name + INHERITOR_SUFFIX;
    }
}

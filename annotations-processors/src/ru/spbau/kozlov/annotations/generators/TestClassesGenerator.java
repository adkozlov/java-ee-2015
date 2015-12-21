package ru.spbau.kozlov.annotations.generators;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.annotations.Test;
import ru.spbau.kozlov.annotations.TestLevel;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author adkozlov
 */
public class TestClassesGenerator extends AbstractClassesGenerator {

    @NotNull
    public static final String TEST_CLASS_SUFFIX = "Test";
    @NotNull
    public static final String RUN_METHOD_NAME = "run";
    @NotNull
    public static final String RUN_TEST_METHOD_NAME = "runTest";
    @NotNull
    public static final String TEST_PASSED_MESSAGE = "passed";

    @NotNull
    private final Map<TestLevel, List<ExecutableElement>> testsByLevel = new LinkedHashMap<>();

    public TestClassesGenerator(@NotNull Elements elements,
                                @NotNull TypeElement typeElement,
                                @NotNull Stream<ExecutableElement> executableElements) {
        super(elements, typeElement);

        for (TestLevel testLevel : TestLevel.values()) {
            testsByLevel.put(testLevel, new ArrayList<>());
        }
        executableElements.forEach(executableElement -> {
            TestLevel testLevel = executableElement.getAnnotation(Test.class).testLevel();
            testsByLevel.get(testLevel).add(executableElement);
        });
    }

    @Override
    public void printMethods(@NotNull IndentedWriter indentedWriter) throws IOException {
        printRunMethod(indentedWriter);
        indentedWriter.println();
        printRunTestMethod(indentedWriter);
    }

    private void printRunMethod(@NotNull IndentedWriter indentedWriter) throws IOException {
        indentedWriter.printf("public static void %s(%s testLevel)", RUN_METHOD_NAME, TestLevel.class.getName());
        indentedWriter.printlnLeftBrace();
        indentedWriter.printf("System.out.println(\"%s\");", getSimpleClassName());
        indentedWriter.println();
        indentedWriter.println();
        printTestMethods(indentedWriter);
        indentedWriter.printlnRightBrace();
    }

    private void printRunTestMethod(@NotNull IndentedWriter indentedWriter) throws IOException {
        indentedWriter.printf("private static void %s(String testName, %s<?> consumer)", RUN_TEST_METHOD_NAME, Consumer.class.getName());
        indentedWriter.printlnLeftBrace();
        indentedWriter.printf("String message = \"%s\";", TEST_PASSED_MESSAGE);
        indentedWriter.println();
        indentedWriter.print("try");
        indentedWriter.printlnLeftBrace();
        indentedWriter.println("consumer.accept(null);");
        indentedWriter.printRightBrace();
        indentedWriter.append(" catch (AssertionError e)");
        indentedWriter.printlnLeftBrace();
        indentedWriter.println("message = e.getMessage();");
        indentedWriter.printlnRightBrace();
        indentedWriter.println("System.out.println(testName + \" \" + message);");
        indentedWriter.printlnRightBrace();
    }

    private void printTestMethods(@NotNull IndentedWriter indentedWriter) throws IOException {
        String className = TestLevel.class.getName();

        printTestMethodByLevel(indentedWriter, testsByLevel.get(TestLevel.CRITICAL));

        List<ExecutableElement> mediumLevelTests = testsByLevel.get(TestLevel.MEDIUM);
        if (!mediumLevelTests.isEmpty()) {
            indentedWriter.printf("if (testLevel == %s.%s || testLevel == %s.%s)",
                    className,
                    TestLevel.MEDIUM.name(),
                    className,
                    TestLevel.LOW.name());
            indentedWriter.printlnLeftBrace();
            printTestMethodByLevel(indentedWriter, mediumLevelTests);
            indentedWriter.printlnRightBrace();
        }

        List<ExecutableElement> lowLevelTests = testsByLevel.get(TestLevel.LOW);
        if (!lowLevelTests.isEmpty()) {
            indentedWriter.printf("if (testLevel == %s.%s)", className, TestLevel.LOW);
            indentedWriter.printlnLeftBrace();
            printTestMethodByLevel(indentedWriter, lowLevelTests);
            indentedWriter.printlnRightBrace();
        }
    }

    private void printTestMethodByLevel(@NotNull IndentedWriter indentedWriter, @NotNull List<ExecutableElement> executableElements) throws IOException {
        for (ExecutableElement executableElement : executableElements) {
            Test test = executableElement.getAnnotation(Test.class);
            indentedWriter.printf("runTest(\"%s\", ignored -> %s.%s());",
                    test.testName(),
                    getTypeElement().getQualifiedName(),
                    executableElement.getSimpleName());
            indentedWriter.println();
        }
    }

    @NotNull
    @Override
    protected String appendSuffix(@NotNull Name name) {
        return getTestClassName(name);
    }

    @NotNull
    public static String getTestClassName(@NotNull Name name) {
        return name + TEST_CLASS_SUFFIX;
    }
}

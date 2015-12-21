package ru.spbau.kozlov.annotations.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbau.kozlov.annotations.TestLevel;

import java.io.IOException;
import java.util.List;

/**
 * @author adkozlov
 */
public class TestUnitsGenerator implements IClassesGenerator {

    @NotNull
    private final String name;
    @NotNull
    private final List<String> tests;

    @NotNull
    private final String simpleName;
    @Nullable
    private final String packageName;

    public TestUnitsGenerator(@NotNull String name, @NotNull List<String> tests) {
        this.name = name;
        this.tests = tests;

        int index = name.lastIndexOf('.');
        if (index != -1) {
            simpleName = name.substring(index + 1);
            packageName = name.substring(0, index);
        } else {
            simpleName = name;
            packageName = null;
        }
    }

    @NotNull
    @Override
    public String getClassName() {
        return name;
    }

    @NotNull
    @Override
    public String getSimpleClassName() {
        return simpleName;
    }

    @Nullable
    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void printMethods(@NotNull IndentedWriter indentedWriter) throws IOException {
        printMainMethod(indentedWriter);
        indentedWriter.println();
        printPrintUsageMethod(indentedWriter);
    }

    private void printMainMethod(@NotNull IndentedWriter indentedWriter) throws IOException {
        String testLevelClassName = TestLevel.class.getName();

        indentedWriter.printf("public static void main(String[] args)");
        indentedWriter.printlnLeftBrace();
        indentedWriter.print("if (args.length != 1)");
        indentedWriter.printlnLeftBrace();
        indentedWriter.println("printUsage();");
        indentedWriter.println("return;");
        indentedWriter.printlnRightBrace();
        indentedWriter.println();
        indentedWriter.printf("%s testLevel;", testLevelClassName);
        indentedWriter.println();
        indentedWriter.print("try");
        indentedWriter.printlnLeftBrace();
        indentedWriter.printf("testLevel = %s.valueOf(args[0]);", testLevelClassName);
        indentedWriter.println();
        indentedWriter.printRightBrace();
        indentedWriter.append(" catch (NullPointerException | IllegalArgumentException e)");
        indentedWriter.printlnLeftBrace();
        indentedWriter.println("printUsage();");
        indentedWriter.println("return;");
        indentedWriter.printlnRightBrace();
        indentedWriter.println();
        for (String test : tests) {
            indentedWriter.printf("%s.run(testLevel);", test);
            indentedWriter.println();
        }
        indentedWriter.printlnRightBrace();
    }

    private void printPrintUsageMethod(@NotNull IndentedWriter indentedWriter) throws IOException {
        TestLevel[] testLevelValues = TestLevel.values();

        indentedWriter.print("private static void printUsage()");
        indentedWriter.printlnLeftBrace();
        indentedWriter.printf("System.out.println(\"Usage: %s [ %s", getSimpleClassName(), testLevelValues[0].name());
        for (int i = 1; i < testLevelValues.length; i++) {
            indentedWriter.append(" | ");
            indentedWriter.append(testLevelValues[i].name());
        }
        indentedWriter.append(" ]\");");
        indentedWriter.printlnRightBrace();
    }
}

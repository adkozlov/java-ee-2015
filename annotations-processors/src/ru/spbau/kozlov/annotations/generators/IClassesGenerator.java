package ru.spbau.kozlov.annotations.generators;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import java.io.IOException;

/**
 * @author adkozlov
 */
public interface IClassesGenerator {

    @NotNull
    String getSimpleClassName();

    @NotNull
    String getClassName();

    @Nullable
    String getPackageName();

    @Nullable
    default String getBaseClassName() {
        return null;
    }

    default void generate(@NotNull Filer filer) throws IOException {
        try (IndentedWriter indentedWriter = new IndentedWriter(filer, getClassName())) {
            String packageName = getPackageName();
            if (packageName != null) {
                indentedWriter.printf("package %s;", packageName);
                indentedWriter.println();
                indentedWriter.println();
            }

            indentedWriter.printf("public class %s", getSimpleClassName());
            String baseClassName = getBaseClassName();
            if (baseClassName != null) {
                indentedWriter.append(" extends ");
                indentedWriter.append(baseClassName);
            }
            indentedWriter.printlnLeftBrace();
            indentedWriter.println();

            printMethods(indentedWriter);

            indentedWriter.printlnRightBrace();
        } catch (FilerException e) {
            // ignoring
        }
    }

    void printMethods(@NotNull IndentedWriter indentedWriter) throws IOException;
}

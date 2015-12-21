package ru.spbau.kozlov.annotations.generators;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.Filer;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;

/**
 * @author adkozlov
 */
public class IndentedWriter implements Closeable {

    @NotNull
    private final BufferedWriter bufferedWriter;

    @NotNull
    private String tab = "\t";
    private int indent = 0;

    public IndentedWriter(@NotNull Filer filer, @NotNull String className) throws IOException {
        bufferedWriter = new BufferedWriter(filer.createSourceFile(className).openWriter());
    }

    @NotNull
    public String getTab() {
        return tab;
    }

    public void setTab(@NotNull String tab) {
        this.tab = tab;
    }

    @Override
    public void close() throws IOException {
        bufferedWriter.close();
    }

    public void printlnLeftBrace() throws IOException {
        bufferedWriter.write(" {");
        println();
        indent++;
    }

    public void printRightBrace() throws IOException {
        if (indent == 0) {
            throw new RuntimeException("Indent cannot be decreased");
        }
        indent--;
        print("}");
    }

    public void printlnRightBrace() throws IOException {
        printRightBrace();
        println();
    }

    public void append(@NotNull String string) throws IOException {
        bufferedWriter.write(string);
    }

    public void print(@NotNull String string) throws IOException {
        appendIndent();
        append(string);
    }

    public void println() throws IOException {
        bufferedWriter.newLine();
    }

    public void println(@NotNull String string) throws IOException {
        print(string);
        println();
    }

    public void printf(@NotNull String format, @NotNull Object... args) throws IOException {
        appendIndent();
        append(String.format(format, args));
    }

    private void appendIndent() throws IOException {
        for (int i = 0; i < indent; i++) {
            bufferedWriter.write(tab);
        }
    }
}

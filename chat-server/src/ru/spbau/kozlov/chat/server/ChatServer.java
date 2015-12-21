package ru.spbau.kozlov.chat.server;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.chat.server.services.ChatServerResourceConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * @author adkozlov
 */
public class ChatServer implements Closeable {

    @NotNull
    public static final Logger LOGGER = Logger.getLogger(ChatServer.class.getName());

    @NotNull
    private final HttpServer httpServer;

    public ChatServer(int port) throws NoSuchAlgorithmException, IOException {
        SSLContext sslContext = SSLContext.getDefault();
        SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
        sslContext.createSSLEngine().setSSLParameters(sslParameters);

        httpServer = GrizzlyHttpServerFactory.createHttpServer(UriBuilder.fromUri("https://localhost")
                        .port(port)
                        .build(),
                new ChatServerResourceConfig(),
                true,
                new SSLEngineConfigurator(sslContext)
                        .setClientMode(false)
                        .setNeedClientAuth(true));
        httpServer.start();
    }

    @Override
    public void close() {
        httpServer.shutdownNow();
    }

    public static void main(@NotNull String[] args) {
        String usage = "usage: ChatServer <port>";
        if (args.length < 1) {
            LOGGER.log(Level.INFO, usage);
        }

        try (ChatServer ignored = new ChatServer(parseInt(args[0]));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            bufferedReader.lines().collect(Collectors.toList());
        } catch (NoSuchAlgorithmException | IOException e) {
            LOGGER.log(Level.SEVERE, "error during initialization", e);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.INFO, usage);
        }
    }
}

package ru.spbau.kozlov.chat.client;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.HttpUrlConnectorProvider;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;
import static java.lang.String.join;

/**
 * @author adkozlov
 */
public class ChatClient implements Closeable {

    static {
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> hostname.equals("localhost"));
    }

    @NotNull
    public static final Logger LOGGER = Logger.getLogger(ChatClient.class.getName());
    @NotNull
    public static final MediaType[] EXPECTED_MEDIA_TYPES = {MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE};

    @NotNull
    public final Client client;
    @NotNull
    private final WebTarget webTarget;

    public ChatClient(@NotNull String address, int port) throws NoSuchAlgorithmException {
        client = ClientBuilder.newBuilder()
                .withConfig(new ClientConfig().connectorProvider(new HttpUrlConnectorProvider()))
                .sslContext(SSLContext.getDefault())
                .build();
        webTarget = client.target(UriBuilder.fromUri("https://" + address)
                .port(port)
                .build());
    }

    @Override
    public void close() {
        client.close();
    }

    public boolean handleCommand(@NotNull String command) {
        List<String> query = Arrays.asList(command.split("\\s"));
        List<String> segments = query.subList(1, query.size());

        switch (first(query)) {
            case "get":
                handleGet(segments);
                break;
            case "post":
                handlePost(segments);
                break;
            case "quit":
                return false;
            default:
                LOGGER.log(Level.INFO, "[ get | post | quit ]");
        }

        return true;
    }

    private void handleGet(@NotNull List<String> segments) {
        if (segments.isEmpty()) {
            LOGGER.log(Level.INFO, "get [ person | message | invoke ]");
            return;
        }

        WebTarget webTarget = this.webTarget;
        for (String segment : segments) {
            webTarget = webTarget.path(segment);
        }

        printResponse(webTarget.request(EXPECTED_MEDIA_TYPES).get());
    }

    private void handlePost(@NotNull List<String> arguments) {
        if (arguments.isEmpty()) {
            LOGGER.log(Level.INFO, "post [ person | message | invoke ]");
            return;
        }

        Response response = null;
        switch (first(arguments)) {
            case "person":
                for (String username : subList(arguments, 1)) {
                    response = webTarget.path("person")
                            .path(username)
                            .request(EXPECTED_MEDIA_TYPES)
                            .post(Entity.entity(username, MediaType.TEXT_PLAIN));
                }
                if (arguments.size() < 2) {
                    LOGGER.log(Level.INFO, "post person <username>");
                }
                break;
            case "message":
                if (arguments.size() >= 4) {
                    response = webTarget.path("message")
                            .path(arguments.get(1))
                            .path(arguments.get(2))
                            .request(EXPECTED_MEDIA_TYPES)
                            .post(Entity.entity(join(" ", subList(arguments, 3)), MediaType.TEXT_PLAIN));
                } else {
                    LOGGER.log(Level.INFO, "post message <fromId> <toId> <text>");
                }
                break;
            case "invoke":
                if (arguments.size() >= 2) {
                    response = webTarget.path("invoke")
                            .path(arguments.get(1))
                            .path(join(" ", subList(arguments, 2)))
                            .request(EXPECTED_MEDIA_TYPES)
                            .post(Entity.entity(new byte[0], MediaType.TEXT_PLAIN));
                } else {
                    LOGGER.log(Level.INFO, "post invoke <personId> <command>");
                }
                break;
        }

        if (response != null) {
            printResponse(response);
        }
    }

    @NotNull
    private List<String> subList(@NotNull List<String> strings, int from) {
        return strings.subList(from, strings.size());
    }

    @NotNull
    private static String first(@NotNull List<String> strings) {
        return strings.get(0).toLowerCase().trim();
    }

    private static void printResponse(@NotNull Response response) {
        LOGGER.log(Level.INFO, response.toString() + ": " + response.readEntity(String.class));
    }

    public static void main(@NotNull String[] args) {
        String usage = "usage: ChatClient <address> <port>";
        if (args.length < 2) {
            LOGGER.log(Level.INFO, usage);
        }

        try (ChatClient chatClient = new ChatClient(args[0], parseInt(args[1]));
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))) {
            bufferedReader.lines().map(chatClient::handleCommand).filter(result -> !result).findFirst();
        } catch (NoSuchAlgorithmException e) {
            LOGGER.log(Level.SEVERE, "error during initialization", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "I/O error", e);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.INFO, usage);
        }
    }
}

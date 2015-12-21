package ru.spbau.kozlov.chat.server.services;

import org.jetbrains.annotations.NotNull;
import ru.spbau.kozlov.chat.server.PersistenceUnit;
import ru.spbau.kozlov.chat.server.entities.InvocationRequest;
import ru.spbau.kozlov.chat.server.entities.InvocationResponse;
import ru.spbau.kozlov.chat.server.entities.Person;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.h2.util.IOUtils.copyAndCloseInput;
import static org.h2.util.IOUtils.readBytesAndClose;

/**
 * @author adkozlov
 */
@Path("/invoke")
@Produces({"application/json", "application/xml"})
public class InvocationService {

    @NotNull
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Inject
    private PersistenceUnit persistenceUnit;

    @POST
    @Path("{personId: \\d+}/{command: .+}")
    public InvocationRequest postRequest(@PathParam("personId") long personId,
                                         @PathParam("command") String command,
                                         byte[] inputStream) {
        InvocationRequest result = persistenceUnit.transactional(entityManager -> {
            InvocationRequest request = new InvocationRequest();
            request.setPerson(entityManager.find(Person.class, personId));
            request.setCommand(command);
            request.setInputStream(inputStream);

            entityManager.persist(request);
            return request;
        });

        execute(result);
        return result;
    }

    @GET
    @Path("{requestId: \\d+}")
    public InvocationResponse getResponse(@PathParam("requestId") long requestId) {
        return persistenceUnit.transactional(
                entityManager -> entityManager.createNamedQuery("InvocationResponse.selectByRequestId", InvocationResponse.class)
                        .setParameter("requestId", requestId)
                        .getSingleResult());
    }

    private void execute(@NotNull InvocationRequest request) {
        executorService.submit(() -> {
            int exitValue = -1;
            byte[] outputStream = new byte[0];
            byte[] errorStream = new byte[0];
            try {
                Process process = Runtime.getRuntime().exec(request.getCommand());
                copyAndCloseInput(new ByteArrayInputStream(request.getInputStream()), process.getOutputStream());
                process.waitFor();

                exitValue = process.exitValue();
                outputStream = readBytesAndClose(process.getInputStream(), -1);
                errorStream = readBytesAndClose(process.getErrorStream(), -1);
            } catch (IOException | InterruptedException e) {
                byte[] message = e.getLocalizedMessage().getBytes();
                ByteBuffer byteBuffer = ByteBuffer.allocate(errorStream.length + message.length);
                byteBuffer.put(errorStream);
                byteBuffer.put(message);
                errorStream = byteBuffer.array();
            } finally {
                InvocationResponse response = new InvocationResponse();
                response.setRequest(request);
                response.setExitValue(exitValue);
                response.setOutputStream(outputStream);
                response.setErrorStream(errorStream);

                persistenceUnit.transactional(entityManager -> {
                    entityManager.persist(response);
                    return true;
                });
            }
        });
    }
}

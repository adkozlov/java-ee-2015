package ru.spbau.kozlov.chat.server.services;

import ru.spbau.kozlov.chat.server.PersistenceUnit;
import ru.spbau.kozlov.chat.server.entities.Message;
import ru.spbau.kozlov.chat.server.entities.Person;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

/**
 * @author adkozlov
 */
@Path("/message")
@Produces({"application/json", "application/xml"})
public class MessageService {

    @Inject
    private PersistenceUnit persistenceUnit;

    @GET
    @Produces({"application/json", "application/xml"})
    public List<Message> getMessages() {
        return persistenceUnit.transactional(
                entityManager -> entityManager.createNamedQuery("Message.selectAll", Message.class)
                        .getResultList());
    }

    @GET
    @Path("{fromId: \\d+}/{toId: \\d+}")
    public List<Message> getMessagesByPersons(@PathParam("fromId") long fromId,
                                              @PathParam("toId") long toId) {
        return persistenceUnit.transactional(
                entityManager -> entityManager.createNamedQuery("Message.selectByPersons", Message.class)
                        .setParameter("fromId", fromId)
                        .setParameter("toId", toId)
                        .getResultList());
    }

    @GET
    @Path("{fromId: \\d+}/{toId: \\d+}/{fromTimestamp: \\d+}/{toTimestamp: \\d+}")
    public List<Message> getMessagesInRange(@PathParam("fromId") long fromId,
                                            @PathParam("toId") long toId,
                                            @PathParam("fromTimestamp") long fromTimestamp,
                                            @PathParam("toTimestamp") long toTimestamp) {
        return persistenceUnit.transactional(
                entityManager -> entityManager.createNamedQuery("Message.selectInRange", Message.class)
                        .setParameter("fromId", fromId)
                        .setParameter("toId", toId)
                        .setParameter("fromTimestamp", fromTimestamp)
                        .setParameter("toTimestamp", toTimestamp)
                        .getResultList());
    }

    @POST
    @Path("{fromId: \\d+}/{toId: \\d+}")
    public Message postMessage(@PathParam("fromId") long fromId,
                               @PathParam("toId") long toId,
                               String text) {
        return persistenceUnit.transactional(entityManager -> {
            Message message = new Message();
            message.setFrom(entityManager.find(Person.class, fromId));
            message.setTo(entityManager.find(Person.class, toId));
            message.setText(text);

            entityManager.persist(message);
            return message;
        });
    }

}

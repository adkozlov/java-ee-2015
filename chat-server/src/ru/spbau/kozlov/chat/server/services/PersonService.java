package ru.spbau.kozlov.chat.server.services;

import ru.spbau.kozlov.chat.server.PersistenceUnit;
import ru.spbau.kozlov.chat.server.entities.Person;

import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

/**
 * @author adkozlov
 */
@Path("/person")
@Produces({"application/json", "application/xml"})
public class PersonService {

    @Inject
    private PersistenceUnit persistenceUnit;

    @GET
    public List<Person> getPersons() {
        return persistenceUnit.transactional(
                entityManager -> entityManager.createNamedQuery("Person.selectAll", Person.class)
                        .getResultList());
    }

    @GET
    @Path("{username: [a-zA-Z\\w*]}")
    public Person getPerson(@PathParam("username") String username) {
        return persistenceUnit.transactional(
                entityManager -> entityManager.createNamedQuery("Person.selectByUsername", Person.class)
                        .setParameter("username", username)
                        .getSingleResult());
    }

    @GET
    @Path("{id: \\d+}")
    public Person getPerson(@PathParam("id") long id) {
        return persistenceUnit.transactional(entityManager -> entityManager.find(Person.class, id));
    }

    @POST
    @Path("{username: [a-zA-Z]\\w*}")
    public Person postPerson(@PathParam("username") String username) {
        return persistenceUnit.transactional(entityManager -> {
            Person person = new Person();
            person.setUsername(username);

            entityManager.persist(person);
            return person;
        });
    }
}

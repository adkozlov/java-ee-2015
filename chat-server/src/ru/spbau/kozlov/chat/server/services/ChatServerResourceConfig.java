package ru.spbau.kozlov.chat.server.services;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import ru.spbau.kozlov.chat.server.PersistenceUnit;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

/**
 * @author adkozlov
 */
@ApplicationPath("resources")
public class ChatServerResourceConfig extends ResourceConfig {

    @Inject
    public ChatServerResourceConfig() {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(PersistenceUnit.class).to(PersistenceUnit.class).in(Singleton.class);
            }
        });
        packages("ru.spbau.kozlov.chat.server.services");
    }
}

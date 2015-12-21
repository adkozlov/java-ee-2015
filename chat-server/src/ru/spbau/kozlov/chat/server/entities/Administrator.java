package ru.spbau.kozlov.chat.server.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author adkozlov
 */
@Entity
public class Administrator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "personId")
    @NotNull
    private Person person;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "id=" + id +
                ", person=" + person +
                '}';
    }
}

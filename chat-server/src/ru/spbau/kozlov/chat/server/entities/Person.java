package ru.spbau.kozlov.chat.server.entities;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author adkozlov
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Person.selectAll", query = "FROM Person p"),
        @NamedQuery(name = "Person.selectByUsername", query = "FROM Person p WHERE p.username = :username")
})
@XmlRootElement
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    @Pattern(regexp = "^[a-zA-Z]\\w*$", message = "illegal username")
    @Size(min = 1, max = 16)
    private String username;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}

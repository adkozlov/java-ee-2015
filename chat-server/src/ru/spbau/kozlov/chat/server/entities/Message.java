package ru.spbau.kozlov.chat.server.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * @author adkozlov
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "Message.selectAll", query = "FROM Message m"),
        @NamedQuery(name = "Message.selectByPersons", query = "FROM Message m WHERE m.from.id = :fromId AND m.to.id = :toId"),
        @NamedQuery(name = "Message.selectInRange",
                query = "FROM Message m WHERE m.from.id = :fromId AND m.to.id = :toId AND m.timestamp >= :fromTimestamp AND m.timestamp <= :toTimestamp")
})
@XmlRootElement
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "fromId")
    @NotNull
    private Person from;

    @ManyToOne
    @JoinColumn(name = "toId")
    @NotNull
    private Person to;

    @NotNull
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getFrom() {
        return from;
    }

    public void setFrom(Person from) {
        this.from = from;
    }

    public Person getTo() {
        return to;
    }

    public void setTo(Person to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

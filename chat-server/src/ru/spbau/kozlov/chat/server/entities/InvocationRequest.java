package ru.spbau.kozlov.chat.server.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Date;

/**
 * @author adkozlov
 */
@Entity
@XmlRootElement
public class InvocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "personId")
    @NotNull
    private Person person;

    @Size(min = 1)
    private String command;

    @Lob
    @NotNull
    private byte[] inputStream;

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

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public byte[] getInputStream() {
        return inputStream;
    }

    public void setInputStream(byte[] inputStream) {
        this.inputStream = inputStream;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "InvocationRequest{" +
                "id=" + id +
                ", person=" + person +
                ", command='" + command + '\'' +
                ", inputStream=" + Arrays.toString(inputStream) +
                ", timestamp=" + timestamp +
                '}';
    }
}

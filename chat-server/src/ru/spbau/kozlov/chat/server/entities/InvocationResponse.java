package ru.spbau.kozlov.chat.server.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

/**
 * @author adkozlov
 */
@Entity
@NamedQueries({
        @NamedQuery(name = "InvocationResponse.selectByRequestId", query = "FROM InvocationResponse r WHERE r.request.id = :requestId")
})
@XmlRootElement
public class InvocationResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "requestId")
    @NotNull
    private InvocationRequest request;

    private int exitValue;

    @Lob
    @NotNull
    private byte[] outputStream;

    @Lob
    @NotNull
    private byte[] errorStream;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InvocationRequest getRequest() {
        return request;
    }

    public void setRequest(InvocationRequest request) {
        this.request = request;
    }

    public int getExitValue() {
        return exitValue;
    }

    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    public byte[] getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(byte[] outputStream) {
        this.outputStream = outputStream;
    }

    public byte[] getErrorStream() {
        return errorStream;
    }

    public void setErrorStream(byte[] errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public String toString() {
        return "InvocationResponse{" +
                "id=" + id +
                ", request=" + request +
                ", exitValue=" + exitValue +
                ", outputStream=" + Arrays.toString(outputStream) +
                ", errorStream=" + Arrays.toString(errorStream) +
                '}';
    }
}

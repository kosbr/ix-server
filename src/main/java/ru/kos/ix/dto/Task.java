package ru.kos.ix.dto;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Created by kosbr on 08.04.2016.
 */
public class Task implements Serializable {

    private static final long serialVersionUID = -7401961224124280149L;

    private Integer id;
    private String serviceName;
    private String methodName;
    private Object[] arguments;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(final Object[] arguments) {
        this.arguments = arguments;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("serviceName", serviceName)
                .add("methodName", methodName)
                .add("arguments", arguments)
                .toString();
    }
}

package ru.kos.ix.dto;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Created by Константин on 08.04.2016.
 */
public class Task implements Serializable {

    private final static long serialVersionUID = -7401961224124280149l;

    private Integer id;
    private String serviceName;
    private String methodName;
    private Object[] arguments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
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

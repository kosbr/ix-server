package com.github.kosbr.ix.dto;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Created by kosbr on 08.04.2016.
 */
public class AnsTask implements Serializable {

    private static final long serialVersionUID = -7401961224124280149L;

    private final Integer id;
    private final Object result;
    private final String statusInfo;
    private final Status status;

    public AnsTask(final Integer id, final Object result,
                   final Status status, final String statusInfo) {
        this.id = id;
        this.result = result;
        this.status = status;
        this.statusInfo = statusInfo;
    }

    public Integer getId() {
        return id;
    }

    public Object getResult() {
        return result;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("result", result)
                .add("status", status)
                .add("statusInfo", statusInfo)
                .toString();
    }
}

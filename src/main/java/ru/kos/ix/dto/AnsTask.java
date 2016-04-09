package ru.kos.ix.dto;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Created by Константин on 08.04.2016.
 */
public class AnsTask implements Serializable {

    private final static long serialVersionUID = -7401961224124280149l;

    private Integer id;
    private Object result;
    private String status;

    public AnsTask(Integer id, Object result, String status) {
        this.id = id;
        this.result = result;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public Object getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("result", result)
                .add("status", status)
                .toString();
    }
}

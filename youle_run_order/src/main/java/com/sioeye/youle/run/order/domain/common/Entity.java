package com.sioeye.youle.run.order.domain.common;

import com.sioeye.youle.run.order.domain.common.AssertionConcern;

import java.io.Serializable;

/**
 * 实体类
 */
public class Entity extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 2L;

    private String id;

    public String id() {
        return this.id;
    }

    private void setId(String id) {
        this.id = id;
    }

    protected Entity(String id){
        super();
        setId(id);
    }

}

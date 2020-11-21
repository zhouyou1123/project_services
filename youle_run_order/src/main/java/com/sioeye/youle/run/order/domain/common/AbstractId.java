package com.sioeye.youle.run.order.domain.common;

import java.io.Serializable;

/**
 * 抽象主键
 * @author Wei
 */
public class AbstractId extends AssertionConcern implements Identity , Serializable {

    public static final int ID_LENGTH = 32;
    private static final long serialVersionUID = 1L;

    private String id;

    public String id() {
        return this.id;
    }

    protected AbstractId(String id) {
        this();

        this.setId(id);
    }

    protected void validateId(String id) {
        // 子类实现特殊验证，验证失败需要抛出异常
    }

    protected AbstractId() {
        super();
    }

    private void setId(String id) {
        this.assertArgumentNotEmpty(id, "The basic identity is required.");
        this.assertArgumentLength(id, ID_LENGTH, "The basic identity must be 32 characters.");

        this.validateId(id);

        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractId that = (AbstractId) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AbstractId{" +
                "id='" + id + '\'' +
                '}';
    }
}

package com.sioeye.youle.run.order.domain.goods;

import com.sioeye.youle.run.order.domain.common.AbstractId;
import lombok.Getter;

@Getter
public class Seat extends AbstractId {
    private String sequenceNo;
    private String mark;
    public Seat(String id,String sequenceNo,String mark){
        super(id);
        setSequenceNo(sequenceNo);
        setMark(mark);
    }
    private void setSequenceNo(String sequenceNo){
//        this.assertArgumentNotEmpty(sequenceNo,"the sequence of seat is not empty.");
        this.sequenceNo = sequenceNo;
    }
    private void setMark(String mark){
//        this.assertArgumentNotEmpty(mark,"the mark of seat is not empty.");
        this.mark = mark;
    }

}

package com.sioeye.youle.run.order.gateways.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class ClipDtoRequest {
    private List<String> ids;
    public ClipDtoRequest(String clipId){
        ids = Arrays.asList(clipId);

    }
}

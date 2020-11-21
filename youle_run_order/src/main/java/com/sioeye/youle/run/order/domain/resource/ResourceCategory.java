package com.sioeye.youle.run.order.domain.resource;

public enum ResourceCategory {
    Video(0),Photo(1);

    private Integer code;

    public Integer getCode() {
        return code;
    }
    ResourceCategory(Integer code){
        this.code = code;
    }
    public static ResourceCategory valueOf(Integer code){
       for (ResourceCategory resourceCategory : values()){
           if (resourceCategory.getCode().equals(code)){
               return resourceCategory;
           }
       }
       throw new RuntimeException(String.format("code(%s) can not convert to ResourceCategory.",code));
    }

}

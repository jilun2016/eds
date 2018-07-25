package com.eds.ma.mongodb.collection;

import com.eds.ma.mongodb.BaseDocument;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "sequence")
public class SeqInfo extends BaseDocument {

    @Field
    private String collName;//
    @Field
    private Long seqId;// 序列值

}
 

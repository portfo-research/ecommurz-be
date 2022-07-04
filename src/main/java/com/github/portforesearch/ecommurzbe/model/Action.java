package com.github.portforesearch.ecommurzbe.model;

import lombok.*;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@Setter
@Getter
@MappedSuperclass
@NoArgsConstructor
public  class Action {
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
    private int recordStatusId;
}

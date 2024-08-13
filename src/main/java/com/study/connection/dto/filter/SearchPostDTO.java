package com.study.connection.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchPostDTO {

    private String startDate;
    private String endDate;
    private int categoryId;
    private String keywords;

}

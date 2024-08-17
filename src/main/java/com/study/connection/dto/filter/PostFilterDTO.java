package com.study.connection.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostFilterDTO {
    private String startDate;
    private String endDate;
    private int categoryId;
    private String keyword;
    private int offset;


    public void setOffset(int currentPage, int limit) {
        this.offset = (currentPage - 1) * limit;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
// TODO 수정한 DTO
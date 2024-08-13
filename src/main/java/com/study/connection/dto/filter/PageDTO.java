package com.study.connection.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {
    private int startIndex;
    private int endIndex;
    private int currentIndex;
    private int startPostNumber;
    private int endPostNumber;
    private int totalPostNumber;
}

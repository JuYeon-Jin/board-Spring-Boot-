package com.study.connection.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListDTO {
    private int id;
    private String title;
    private String createdAt;
    private String updatedAt;
    private String writer;
    private int views;
    private String categoryName;
    private boolean fileExist;
}

package com.study.connection.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostArticleDTO {
    private int postId;
    private String title;
    private String content;
    private String createdAt;
    private String updatedAt;
    private String writer;
    private int views;
    private String categoryName;
}

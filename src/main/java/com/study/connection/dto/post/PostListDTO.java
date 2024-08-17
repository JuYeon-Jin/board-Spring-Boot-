package com.study.connection.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListDTO {
    private int postId;
    private String title;
    private String writer;
    private int views;
    private String categoryName;
    private String createdAt;
    private String updatedAt;
    private boolean fileExist;
}
// TODO 수정한 DTO

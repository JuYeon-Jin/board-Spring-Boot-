package com.study.connection.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateDTO {
    private int postId;
    private int categoryId;
    private String writer;
    private String password;
    private String title;
    private String content;
    private List<String> deleteFileId;
}

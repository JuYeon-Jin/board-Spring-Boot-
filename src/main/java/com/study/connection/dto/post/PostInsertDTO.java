package com.study.connection.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostInsertDTO {
    private int postId;
    private int categoryId;
    private String title;
    private String content;
    private String writer;
    private String password;
    private String bCryptPassword;
}
// TODO 수정한 DTO

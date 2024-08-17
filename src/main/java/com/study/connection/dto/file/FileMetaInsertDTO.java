package com.study.connection.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetaInsertDTO {
    private int fileId;
    private int postId;
    private String fileName;
    private long fileSize;
    private String contentType;
    private String filePath;
}
// TODO 수정한 DTO
package com.study.connection.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDownloadDTO {
    private int fileId;
    private String fileName;
    private String filePath;
    private String contentType;
}
// TODO 수정한 DTO

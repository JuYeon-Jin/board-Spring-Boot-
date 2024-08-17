package com.study.connection.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDTO {
    int fileId;
    String fileName;
    String fileSize;
}
// TODO 수정한 DTO

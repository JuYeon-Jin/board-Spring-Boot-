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
    private String fileName;       // 파일 이름
    private long fileSize;         // 파일 크기
    private String contentType;    // MIME 타입
    private byte[] data;           // 파일 데이터
}

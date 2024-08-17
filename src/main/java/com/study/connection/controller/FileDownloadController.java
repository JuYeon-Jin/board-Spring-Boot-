package com.study.connection.controller;

import com.study.connection.dao.PostDAO;
import com.study.connection.dto.file.FileDownloadDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;

@Slf4j
@Controller
public class FileDownloadController {

    private final PostDAO postDAO;

    public FileDownloadController(PostDAO postDAO) {
        this.postDAO = postDAO;
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("seq") int fileId) {

        try {
            FileDownloadDTO dto = postDAO.getFileDownloadData(fileId);
            File file = new File(dto.getFilePath());

            if (!file.exists()) {
                throw new RuntimeException("File not found: " + dto.getFileName());
            }

            byte[] fileBytes = Files.readAllBytes(file.toPath());

            // TODO 왜 파일이름을 인코딩해야하는지? 인코딩 안하면 어떻게 되는지?
            String encodedFileName = URLEncoder.encode(dto.getFileName(), "UTF-8").replace("+", "%20");

            // TODO 헤더에 왜 이 데이터들이 담겨야 하는지? 안담기면 어떻게 되는지?
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(fileBytes.length);
            headers.setContentDispositionFormData("attachment", encodedFileName);

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            log.error("Error occurred while downloading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}

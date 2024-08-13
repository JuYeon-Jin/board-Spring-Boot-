package com.study.connection.controller;

import com.study.connection.dto.file.FileDownloadDTO;
import com.study.connection.service.FileDownloadService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
public class FileDownloadController {

    private FileDownloadService service;

    public FileDownloadController(FileDownloadService service) {
        this.service = service;
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam("seq") int fileId, HttpServletResponse response) {
        System.out.println("fileId = " + fileId);

        FileDownloadDTO dto = service.downloadFile(fileId);

       if (dto != null) {
           try {
               response.setContentType(dto.getContentType());
               response.setContentLength((int) dto.getFileSize());

               String encodedFileName = URLEncoder.encode(dto.getFileName(), "UTF-8").replace("+", "%20");
               response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

               ServletOutputStream outputStream = response.getOutputStream();

               outputStream.write(dto.getData());
               outputStream.flush();
               outputStream.close();
           } catch (IOException e) {
               log.error(e.getMessage());
           }
       } else {
           response.setStatus(HttpServletResponse.SC_NOT_FOUND);
       }
    }
}

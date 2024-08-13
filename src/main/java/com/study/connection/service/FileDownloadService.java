package com.study.connection.service;

import com.study.connection.dao.FileDownloadDAO;
import com.study.connection.dto.file.FileDownloadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileDownloadService {

    private FileDownloadDAO fileDownloadDAO;

    public FileDownloadService(FileDownloadDAO fileDownloadDAO) {
        this.fileDownloadDAO = fileDownloadDAO;
    }

    public FileDownloadDTO downloadFile(int fileId) {
        try {
            return fileDownloadDAO.downloadFile(fileId);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

}

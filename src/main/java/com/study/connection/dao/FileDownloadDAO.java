package com.study.connection.dao;

import com.study.connection.dto.file.FileDownloadDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface FileDownloadDAO {

    @Select("SELECT file_name AS fileName , file_size AS fileSize, content_type AS contentType, data" +
            " FROM files" +
            " WHERE id = #{fileId}")
    public FileDownloadDTO downloadFile(@Param("fileId") int fileId);

}

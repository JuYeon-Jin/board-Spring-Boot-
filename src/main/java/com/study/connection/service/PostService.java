package com.study.connection.service;

import com.study.connection.dao.PostDAO;
import com.study.connection.dto.file.*;
import com.study.connection.dto.filter.CategoryDTO;
import com.study.connection.dto.filter.PageDTO;
import com.study.connection.dto.filter.PostFilterDTO;
import com.study.connection.dto.post.*;
import com.study.connection.handler.exception.InvalidPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * 게시물 관련, 데이터베이스와 상호작용을 포함한 비즈니스 로직을 처리합니다.
 * 게시물의 조회, 등록, 수정, 삭제 및 파일 업로드 등을 담당합니다.
 * 쭉
 * 쭉
 * 쭉
 * 메소드 목차 간략히 작성하기
 */
@Slf4j
@Service
public class PostService {

    private final String uploadDir = "src/main/resources/upload/file1/";
    private final String uniqueKey = "-";
    private final long maxDirectorySize = 500L * 1024L * 1024L; // 500MB

    private PostDAO postDAO;

    public PostService(PostDAO postDAO) {
        this.postDAO = postDAO;
    }


    // TODO 모든 예외 추후에 일괄적으로 Global Exception Handler 처리
    //  (InvalidPasswordException)  생성
    /**
     * 데이터베이스에서 전체 게시물 수를 가져옵니다.
     * 검색 조건이 있다면, 검색 조건을 반영한 전체 게시물 수를 가져옵니다.
     *
     * @return {@code CategoryDTO} 객체들의 리스트를 반환합니다. 예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public int getAllPostsCount(PostFilterDTO dto) {
        return postDAO.countAllPosts(dto);
    }



    /**
     * 데이터베이스에서 카테고리 목록을 가져옵니다.
     * 가져오는 중에 예외가 발생하면, 빈 리스트를 반환합니다.
     *
     * @return {@code CategoryDTO} 객체들의 리스트를 반환합니다. 예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public List<CategoryDTO> getCategories() {
        try {
            return postDAO.getCategoryList();
        } catch (Exception e) {
            log.error("getCategoryList 쿼리 실행 중 오류 : {}", e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 주어진 검색조건에 따라 필터링한 데이터 리스트를 반환합니다.
     * 검색조건이 없으면 전체 게시물 리스트를 반환하며, 예외가 발생하면 빈 리스트를 반환합니다.
     *
     * @param dto 게시물 검색 조건을 담고 있는 {@code SearchPostDTO} 객체입니다.
     * @return {@code PostListDTO} 객체들의 리스트를 반환합니다.
     *         예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public List<PostListDTO> getFilteredPosts(PostFilterDTO dto) {
        try {
            // 요청에서 전달된 데이터가 없는 경우에도 searchPostDTO는 절대 null이 되지 않음.
            // 대신, SearchPostDTO 객체는 필드에 기본값(예: null, 0, false 등)을 가지는 빈 객체로 전달됨.
            // String 은 null 또는 '' / int 는 null 또는 0
            // TODO MAPPER.DAO 에서 WHERE 절 조건문 재확인하기
            List<PostListDTO> filteredPosts = postDAO.getFilteredPosts(dto);
            return filteredPosts;

        } catch (Exception e) {
            log.error("getFilteredPosts 쿼리문 실행중 오류 : {}", e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 지정된 게시물 ID에 대한 게시물 정보를 조회하고, 해당 게시물 조회수를 1 증가시킵니다.
     * 예외가 발생하면 null 을 반환합니다.
     *
     * @param postId 조회할 게시물의 ID
     * @return {@link PostArticleDTO} 게시물의 정보를 담고 있는 DTO 객체.
     *         예외가 발생할 경우 null 을 반환합니다.
     */
    public PostArticleDTO getPostDetails(int postId) {
        try {
            PostArticleDTO postArticleDTO = postDAO.getPostDetails(postId);
            // TODO 조회수 관련해서 어떻게 처리할지 고민
            postDAO.addViews(postId);
            return postArticleDTO;
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return null;
        }
    }



    /**
     * 지정된 게시물 ID에 대한 첨부파일 정보(파일 ID, 파일명)를 조회합니다.
     * 가져오는 중에 예외가 발생하면, 빈 리스트를 반환합니다.
     *
     * @return {@code FileInfoDTO} 객체들의 리스트를 반환합니다.
     *         예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public List<FileMetadataDTO> getFileMeta(int postId) {
        try {
            return postDAO.getFileMeta(postId);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 새 게시물을 데이터베이스에 삽입하고 관련 파일을 업로드합니다.
     * 업로드 하는 과정에서 비밀번호를 BCrypt를 사용하여 암호화합니다.
     *
     * @param postInsertDTO 게시물의 세부 정보를 담고 있는 {@link PostInsertDTO} 객체.
     * @param files 업로드할 파일들을 나타내는 {@link MultipartFile} 객체 배열.
     * @throws Exception 비밀번호 암호화 또는 파일 업로드 중 오류가 발생할 경우.
     */
    public void insertPost(PostInsertDTO postInsertDTO, MultipartFile[] files) {
        try {
            postInsertDTO.setBCryptPassword(encryptPassword(postInsertDTO.getPassword()));
            postDAO.insertPost(postInsertDTO);

            int postId = postInsertDTO.getPostId();
            uploadFile(files, postId);

        } catch (Exception e) {
            log.error("Exception occurred insertPost: ", e.getLocalizedMessage());
        }

    }



    /**
     * 지정된 ID의 게시물 데이터의 변경사항을 데이터베이스에 저장하고 관련 첨부 파일을 수정합니다.
     * 업로드 하는 과정에서 비밀번호의 일치 여부를 확인합니다.
     *
     * @param postDTO 게시물의 세부 정보를 담고 있는 {@link PostUpdateDTO} 객체.
     * @param files 업로드할 파일들을 나타내는 {@link MultipartFile} 객체 배열.
     * @throws Exception 비밀번호 암호화 또는 파일 업로드 중 오류가 발생할 경우.
     */
    public void updatePost(PostUpdateDTO postDTO, MultipartFile[] files, DeleteFileIdDTO metaDTO) {
        try {
            boolean result = checkPassword(postDTO.getPassword(), postDTO.getPostId());
            if (result) {
                // TODO 한 게시물에 동일한 파일명 업로드 불가 정책 도입 해야함 (아직 미도입)
                postDAO.updatePost(postDTO);
                int postId = postDTO.getPostId();
                deleteSelectedFiles(postId, metaDTO.getDeleteFileId());
                uploadFile(files, postId);

            } else {
                throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
            }
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public void deletePost(PostDeleteDTO postDTO) {
        try {
            boolean result = checkPassword(postDTO.getPassword(), postDTO.getPostId());
            if (result) {
                int postId = postDTO.getPostId();
                deleteSelectedFiles(postId, postDAO.getFileIdList(postId));
                postDAO.deletePost(postId);
            } else {
                throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }



    /**
     * 제공된 평문 문자열을 BCrypt 해싱 알고리즘을 사용하여 암호화합니다.
     *
     * @param password 암호화할 평문 비밀번호.
     * @return BCrypt로 해시된 비밀번호.
     */
    public String encryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }



    /**
     * 입력된 평문 비밀번호가 DB에 저장된 해시된 비밀번호와 일치하는지 확인합니다.
     *
     * @param plainTextPassword 입력된 평문 비밀번호.
     * @param postId            게시물 ID.
     * @return 비밀번호 일치 여부 (true: 일치, false: 불일치).
     */
    public boolean checkPassword(String plainTextPassword, int postId) {
        String hashedPasswordFromDatabase = postDAO.getPostPassword(postId);
        return BCrypt.checkpw(plainTextPassword, hashedPasswordFromDatabase);
    }



    /**
     * 게시물에 첨부된 파일을 업로드합니다.
     * 먼저 파일의 메타데이터를 DB 에 업로드 한 뒤, 파일데이터를 서버에 업로드 합니다.
     *
     * @param files  업로드할 MultipartFile 배열.
     * @param postId 게시물 ID.
     */
    public void uploadFile(MultipartFile[] files, int postId) throws IOException {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {

                String fileName = file.getOriginalFilename();
                long fileSize = file.getSize();
                String contentType = file.getContentType();
                String filePathStr = uploadDir + postId + uniqueKey + fileName;

                FileMetaInsertDTO metaDto = new FileMetaInsertDTO(0, postId, fileName, fileSize, contentType, filePathStr);
                postDAO.insertFileMeta(metaDto);

                Path filePath = Paths.get(filePathStr);
                Path dirPath = filePath.getParent();
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                Files.write(filePath, file.getBytes());
            }
        }
    }



    /**
     * 선택된 파일의 메타데이터를 DB 에서 삭제하고, 서버에서 삭제합니다.
     * 파일 ID 와 게시물 ID 가 일치하는 경우에만 삭제됩니다.
     *
     * @param fileIdList 삭제할 파일 ID 리스트.
     * @param postId     게시물 ID.
     */
    public void deleteSelectedFiles(int postId, List<String> fileIdList) throws IOException {
        if (fileIdList == null) {
            return;
        }

        for (String fileIdStr : fileIdList) {
            if (fileIdStr != null && !fileIdStr.isEmpty()) {

                int fileId = Integer.parseInt(fileIdStr);
                FileDownloadDTO fileDTO = postDAO.getFileDownloadData(fileId);
                Path filePath = Paths.get(fileDTO.getFilePath());

                if (Files.exists(filePath)) {
                    Files.delete(filePath);

                    FileMetaDeleteDTO metaDTO = new FileMetaDeleteDTO(fileId, postId);
                    postDAO.deleteFile(metaDTO);
                } else {
                    log.error("File not found: {}", filePath.toString());
                }
            }
        }
    }


    /**
     * 게시물의 페이지네이션 정보를 계산합니다.
     *
     * @param totalPostNumber 전체 게시물 수 입니다.
     * @param currentIndex 현재 페이지 인덱스 입니다.
     * @return 페이지네이션 세부 정보를 포함하는 {@link PageDTO} 객체를 반환합니다.
     */
    public PageDTO pagination(int totalPostNumber, int currentIndex) {
        int postLimit = 10;
        int indexLimit = 10;
        int maximumIndex = ((totalPostNumber-1)/postLimit) + 1;

        int startIndex = ((currentIndex-1)/10) + 1;
        int endIndex = startIndex + (indexLimit -1);
        if (endIndex > maximumIndex) {
            endIndex = maximumIndex;
        }

        PageDTO dto = new PageDTO(startIndex, endIndex, currentIndex, totalPostNumber);
        return dto;
    }
}

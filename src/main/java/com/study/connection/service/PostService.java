package com.study.connection.service;

import com.study.connection.dao.PostDAO;
import com.study.connection.dto.file.FileInfoDTO;
import com.study.connection.dto.filter.CategoryDTO;
import com.study.connection.dto.filter.PageDTO;
import com.study.connection.dto.filter.SearchPostDTO;
import com.study.connection.dto.post.PostArticleDTO;
import com.study.connection.dto.post.PostInsertDTO;
import com.study.connection.dto.post.PostListDTO;
import com.study.connection.dto.post.PostUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class PostService {

    private PostDAO postDAO;

    public PostService(PostDAO postDAO) {
        this.postDAO = postDAO;
    }


    /**
     * 데이터베이스에서 모든 게시물의 목록을 가져옵니다.
     * 가져오는 중에 예외가 발생하면 빈 리스트를 반환합니다.
     *
     * @return {@code PostListDTO} 객체들의 리스트를 반환합니다.
     *         예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public List<PostListDTO> getAllPosts() {
        try {
            return postDAO.getPostList();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 주어진 키워드에 따라 게시물을 검색합니다.
     * 만약 카테고리가 전체(CategoryId = 0)인 경우, 모든 카테리에서 게시물을 검색하고,
     * 특정 카테고리가 선택된 경우 해당 카테고리에서 게시물을 검색합니다.
     * 예외가 발생하면 빈 리스트를 반환합니다.
     *
     * @param dto 게시물 검색 조건을 담고 있는 {@code SearchPostDTO} 객체입니다.
     * @return {@code PostListDTO} 객체들의 리스트를 반환합니다.
     *         예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public List<PostListDTO> searchPosts(SearchPostDTO dto) {
        try {
            String startDate = dto.getStartDate();
            String endDate = dto.getEndDate();
            String keyword = dto.getKeywords();
            int categoryId = dto.getCategoryId();

            if (categoryId == 0) {
                log.info("전체 카테고리 검색: {}", keyword);
                return postDAO.searchPostsInAllCategories(startDate, endDate, keyword);
            } else {
                log.info("카테고리 {}번 검색: {}", categoryId, keyword);
                return postDAO.searchPostsInCategory(startDate, endDate, categoryId, keyword);
            }

        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 데이터베이스에서 카테고리 목록을 가져옵니다.
     * 가져오는 중에 예외가 발생하면, 빈 리스트를 반환합니다.
     *
     * @return {@code CategoryDTO} 객체들의 리스트를 반환합니다.
     *         예외가 발생할 경우 빈 리스트를 반환합니다.
     */
    public List<CategoryDTO> getCategories() {
        try {
            return postDAO.getCategoryList();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 게시물의 페이지네이션 정보를 계산합니다.
     *
     * @param allPostsNumber 전체 게시물 수 입니다.
     * @param currentIndex 현재 페이지 인덱스 입니다.
     * @return 페이지네이션 세부 정보를 포함하는 {@link PageDTO} 객체를 반환합니다.
     */
    public PageDTO pagination(int allPostsNumber, int currentIndex) {
        int postNumber = 10;
        int indexNumber = 10;
        int maximumIndex = ((allPostsNumber-1)/indexNumber) + 1;

        int endPost = currentIndex * postNumber;
        int startPost = endPost - (postNumber - 1);
        if (endPost > allPostsNumber) {
            endPost = allPostsNumber;
        }

        int startIndex = ((currentIndex-1)/10) + 1;
        int endIndex = startIndex + (indexNumber -1);
        if (endIndex > maximumIndex) {
            endIndex = maximumIndex;
        }

        PageDTO dto = new PageDTO(startIndex, endIndex, currentIndex, startPost, endPost, allPostsNumber);
        return dto;
    }



    /**
     * 지정된 게시물 ID에 대한 게시물 정보를 조회하고, 해당 게시물 조회수를 1 증가시킵니다.
     * 예외가 발생하면 null 을 반환합니다.
     *
     * @param postId 조회할 게시물의 ID
     * @return {@link PostArticleDTO} 게시물의 정보를 담고 있는 DTO 객체.
     *         예외가 발생할 경우 null 을 반환합니다.
     */
    public PostArticleDTO postArticle(int postId) {
        try {
            PostArticleDTO postArticleDTO = postDAO.readPost(postId);
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
    public List<FileInfoDTO> getFileInfo(int postId) {
        try {
            return postDAO.readFile(postId);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return List.of();
        }
    }



    /**
     * 새 게시물을 데이터베이스에 삽입하고 관련 파일을 업로드합니다.
     * 업로드 하는 과정에서 비밀번호를 BCrypt를 사용하여 암호화합니다.
     *
     * @param postDTO 게시물의 세부 정보를 담고 있는 {@link PostInsertDTO} 객체.
     * @param files 업로드할 파일들을 나타내는 {@link MultipartFile} 객체 배열.
     * @throws Exception 비밀번호 암호화 또는 파일 업로드 중 오류가 발생할 경우.
     */
    public void insertPost(PostInsertDTO postDTO, MultipartFile[] files) {
        try {
            postDTO.setBCryptPassword(encryptPassword(postDTO.getPassword()));
            postDAO.insertPost(postDTO);

            int postId = postDTO.getId();
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
    public void updatePost(PostUpdateDTO postDTO, MultipartFile[] files) {
        try {
            boolean result = checkPassword(postDTO.getPassword(), postDTO.getPostId());
            if (result) {
                postDAO.updatePost(postDTO);

                int postId = postDTO.getPostId();
                deleteSelectedFiles(postDTO.getDeleteFileId(), postId);
                uploadFile(files, postId);

            } else {
                // TODO 게시물 업데이트 실패 로직 수정
                log.error("비밀번호가 일치 하지 않습니다.");
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
        String hashedPasswordFromDatabase = postDAO.passwordFromDatabase(postId);
        return BCrypt.checkpw(plainTextPassword, hashedPasswordFromDatabase);
    }



    /**
     * 게시물에 첨부된 파일을 업로드합니다.
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
                byte[] fileData = file.getBytes();

                postDAO.uploadFile(postId, fileName, fileSize, contentType, fileData);
            }
        }
    }



    /**
     * 선택된 파일들을 DB 에서 삭제합니다.
     * 파일 ID 와 게시물 ID 가 일치하는 경우에만 삭제됩니다.
     *
     * @param fileIdList 삭제할 파일 ID 리스트.
     * @param postId     게시물 ID.
     */
    public void deleteSelectedFiles(List<String> fileIdList, int postId) {
        for (String fileIdStr : fileIdList) {
            if (fileIdStr != null && !fileIdStr.trim().isEmpty()) {
                int fileId = Integer.parseInt(fileIdStr);
                postDAO.deleteFile(fileId, postId);
            }
        }
    }

}

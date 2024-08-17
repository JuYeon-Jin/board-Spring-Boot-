package com.study.connection.dao;

import com.study.connection.dto.file.*;
import com.study.connection.dto.filter.CategoryDTO;
import com.study.connection.dto.filter.PostFilterDTO;
import com.study.connection.dto.post.PostArticleDTO;
import com.study.connection.dto.post.PostInsertDTO;
import com.study.connection.dto.post.PostListDTO;
import com.study.connection.dto.post.PostUpdateDTO;

import java.util.List;

/**
 * 쿼리문은 XML 에 두고 인터페이스에는 메서드 시그니처만 유지
 */
public interface PostDAO {

   // 카테고리 목록을 조회합니다.
   List<CategoryDTO> getCategoryList();

   // 게시물 목록을 조회합니다. (게시물 p JOIN 카테고리 c, 파일 f)
   List<PostListDTO> getFilteredPosts(PostFilterDTO filterDTO);

   // 게시물 전체 갯수를 조회합니다.
   int countAllPosts(PostFilterDTO filterDTO);

   // 게시물 ID를 조건으로 게시물 세부 내용을 조회합니다.
   PostArticleDTO getPostDetails(int postId);

   // 게시물 테이블(post)에 데이터를 저장합니다.
   void insertPost(PostInsertDTO postInsertDTO);

   // 첨부파일의 메타데이터를 파일 테이블(file)에 저장합니다.
   void insertFileMeta(FileMetaInsertDTO fileMetaInsertDTO);

   // 게시물 ID를 조건으로 존재하는 파일의 메타데이터를 조회합니다.
   List<FileMetadataDTO> getFileMeta(int postId);

   // 게시물 ID를 조건으로 존재하는 파일의 ID를 조회합니다.
   List<String> getFileIdList(int postId);

   // 파일 ID를 조건으로 다운로드를 위한 데이터를 조회합니다.
   FileDownloadDTO getFileDownloadData(int fileId);

   // 게시물 ID를 조건으로 해당 게시물을 조회수(views)에 1을 더합니다.
   void addViews(int postId);

   // 게시물 ID를 조건으로 게시물의 비밀번호를 조회합니다.
   String getPostPassword(int postId);

   // 게시물 ID를 조건으로 게시물 테이블(post)에 수정된 데이터를 저장하고, 수정일자(updated_at)를 갱신합니다.
   void updatePost(PostUpdateDTO postUpdateDTO);

   // 게시물 ID를 조건으로 데이터를 삭제합니다.
   void deletePost(int postId);

   // 파일 ID와 게시물 ID를 조건으로 데이터를 삭제합니다.
   void deleteFile(FileMetaDeleteDTO fileMetaDeleteDTO);
}
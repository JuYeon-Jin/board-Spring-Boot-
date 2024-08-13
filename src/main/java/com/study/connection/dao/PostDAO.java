package com.study.connection.dao;

import com.study.connection.dto.file.FileInfoDTO;
import com.study.connection.dto.filter.CategoryDTO;
import com.study.connection.dto.post.PostArticleDTO;
import com.study.connection.dto.post.PostInsertDTO;
import com.study.connection.dto.post.PostListDTO;
import com.study.connection.dto.post.PostUpdateDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface PostDAO {

   @Select("SELECT p.id AS id, p.title AS title, " +
           "DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') AS createdAt, " +
           "DATE_FORMAT(p.updated_at, '%Y-%m-%d %H:%i') AS updatedAt, " +
           "p.writer AS writer, p.views AS views, c.name AS categoryName, " +
           "(SELECT COUNT(*) > 0 FROM files f WHERE f.post_id = p.id) AS fileExist " +
           "FROM posts p JOIN category c " +
           "ON p.category_id = c.id " +
           "WHERE p.is_deleted = false " +
           "ORDER BY p.id DESC")
   public List<PostListDTO> getPostList();

   @Select("SELECT p.id AS id, p.title AS title, " +
           "DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') AS createdAt, " +
           "DATE_FORMAT(p.updated_at, '%Y-%m-%d %H:%i') AS updatedAt, " +
           "p.writer AS writer, p.views AS views, c.name AS categoryName, " +
           "(SELECT COUNT(*) > 0 FROM files f WHERE f.post_id = p.id) AS fileExist " +
           "FROM posts p JOIN category c " +
           "ON p.category_id = c.id " +
           "WHERE p.is_deleted = false " +
           "AND p.created_at BETWEEN STR_TO_DATE(#{startDate}, '%Y-%m-%d') AND STR_TO_DATE(CONCAT(#{endDate}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s') " +
           "AND (p.title LIKE CONCAT('%', #{keyword}, '%') " +
           "OR p.writer LIKE CONCAT('%', #{keyword}, '%') " +
           "OR p.content LIKE CONCAT('%', #{keyword}, '%')) " +
           "ORDER BY p.id DESC")
   public List<PostListDTO> searchPostsInAllCategories(@Param("startDate") String startDate
                                                     , @Param("endDate") String endDate
                                                     , @Param("keyword") String keyword);

   @Select("SELECT p.id AS id, p.title AS title, " +
           "DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') AS createdAt, " +
           "DATE_FORMAT(p.updated_at, '%Y-%m-%d %H:%i') AS updatedAt, " +
           "p.writer AS writer, p.views AS views, c.name AS categoryName, " +
           "(SELECT COUNT(*) > 0 FROM files f WHERE f.post_id = p.id) AS fileExist " +
           "FROM posts p JOIN category c " +
           "ON p.category_id = c.id " +
           "WHERE p.is_deleted = false " +
           "AND p.created_at BETWEEN STR_TO_DATE(#{startDate}, '%Y-%m-%d') AND STR_TO_DATE(CONCAT(#{endDate}, ' 23:59:59'), '%Y-%m-%d %H:%i:%s') " +
           "AND p.category_id = #{categoryId} " +
           "AND (p.title LIKE CONCAT('%', #{keyword}, '%') " +
           "OR p.writer LIKE CONCAT('%', #{keyword}, '%') " +
           "OR p.content LIKE CONCAT('%', #{keyword}, '%')) " +
           "ORDER BY p.id DESC")
   public List<PostListDTO> searchPostsInCategory(@Param("startDate") String startDate
                                                , @Param("endDate") String endDate
                                                , @Param("categoryId") int categoryId
                                                , @Param("keyword") String keyword);

   @Select("SELECT id, name FROM category")
   public List<CategoryDTO> getCategoryList();

   @Insert("INSERT INTO posts (category_id, title, content, writer, password)" +
           " VALUES (#{dto.categoryId}, #{dto.title}, #{dto.content}, #{dto.writer}, #{dto.bCryptPassword})")
   @Options(useGeneratedKeys = true, keyProperty = "id")
   public void insertPost(@Param("dto") PostInsertDTO dto);

   @Insert("INSERT INTO files (post_id, file_name, file_size, content_type, data)" +
           " VALUES (#{postId}, #{fileName}, #{fileSize}, #{contentType}, #{data})")
   public void uploadFile(@Param("postId") int postId,
                          @Param("fileName") String fileName,
                          @Param("fileSize") long fileSize,
                          @Param("contentType") String contentType,
                          @Param("data") byte[] data);

   @Select("SELECT p.id AS postId, p.title AS title, p.content AS content," +
                 " DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i') AS createdAt," +
                 " DATE_FORMAT(p.updated_at, '%Y-%m-%d %H:%i') AS updatedAt," +
                 " p.writer AS writer, p.views AS views, c.name AS categoryName" +
           " FROM posts p JOIN category c" +
                 " ON p.category_id = c.id" +
           " WHERE p.id= #{postId}")
   public PostArticleDTO readPost(@Param("postId") int postId);

   @Update("UPDATE posts SET views = views + 1 WHERE id = #{postId}")
   public void addViews(@Param("postId") int postId);

   @Select("SELECT id AS fileId, file_name AS fileName FROM files WHERE post_id = #{postId}")
   public List<FileInfoDTO> readFile(@Param("postId") int postId);

   @Update("UPDATE posts" +
           " SET category_id = #{dto.categoryId}, title = #{dto.title}, content = #{dto.content}, writer = #{dto.writer}, updated_at = CURRENT_TIMESTAMP" +
           " WHERE id = #{dto.postId}")
   public int updatePost(@Param("dto") PostUpdateDTO dto);

   @Select("SELECT password FROM posts WHERE id = #{postId}")
   public String passwordFromDatabase(@Param("postId") int postId);

   @Delete("DELETE FROM files WHERE id = #{fileId} AND post_id = #{postId}")
   public void deleteFile(@Param("fileId") int fileId, @Param("postId") int postId);


}

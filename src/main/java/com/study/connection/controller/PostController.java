package com.study.connection.controller;

import com.study.connection.dto.filter.PageDTO;
import com.study.connection.dto.filter.SearchPostDTO;
import com.study.connection.dto.post.PostInsertDTO;
import com.study.connection.dto.post.PostListDTO;
import com.study.connection.dto.post.PostUpdateDTO;
import com.study.connection.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PostController {

    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }


    /**
     * 페이지네이션을 적용한 게시물 목록을 반환합니다.
     * 검색 기능을 지원하며, 검색 파라미터가 주어지지 않으면 전체 게시물 목록을 반환합니다.
     *
     * @param model Spring MVC의 `Model` 객체로, 뷰에 전달할 데이터가 포함됩니다.
     * @param searchPostDTO 게시물 검색에 사용되는 조건을 포함하는 데이터 전송 객체입니다. 검색 조건이 없는 경우에도 필드에 기본값이 설정됩니다.
     * @param currentPage 현재 페이지 번호입니다. 요청 파라미터로 전달되며, 기본값은 1입니다.
     * @return 뷰의 경로와 이름을 문자열로 return 합니다.
     */
    @GetMapping("/posts")
    public String getPostList(Model model,
                              @ModelAttribute SearchPostDTO searchPostDTO,
                              @RequestParam(value = "page", required = false, defaultValue = "1") int currentPage) {

        // 요청에서 전달된 데이터가 없는 경우에도 searchPostDTO는 절대 null이 되지 않음.
        // 대신, SearchPostDTO 객체는 필드에 기본값(예: null, 0, false 등)을 가지는 빈 객체로 전달됨.
        // TODO 검색 요청 전달과 전체 검색을 나누는 분기처리 조건문 수정
        List<PostListDTO> postList;
        if (searchPostDTO.getStartDate() == null || searchPostDTO.getStartDate().trim().isEmpty()) {
            postList = postService.getAllPosts();
        } else {
            postList = postService.searchPosts(searchPostDTO);
        }

        PageDTO pageDTO = postService.pagination(postList.size(), currentPage);
        model.addAttribute("page", pageDTO);
        model.addAttribute("postList", postList.subList(pageDTO.getStartPostNumber()-1, pageDTO.getEndPostNumber()));
        model.addAttribute("categories", postService.getCategories());
        // TODO 검색 조건 같이 넘기기
        // model.addAttribute("search", searchPostDTO);

        return "views/post-list";
    }



    /**
     * 지정된 게시물 ID 에 대한 내용을 조회하여 반환합니다.
     * 첨부파일이 포함되어 있다면 파일정보(fileId, fileName) 을 같이 반환하며,
     * 첨부파일이 없다면 빈 목록을 반환합니다.
     *
     * @param model Spring MVC의 `Model` 객체로, 뷰에 전달할 데이터가 포함됩니다.
     * @param postId 게시물의 고유 ID 입니다.
     * @return 뷰의 경로와 이름을 문자열로 return 합니다.
     */
    @GetMapping("/post")
    public String getPostArticle(Model model, @RequestParam("postId") int postId) {
        model.addAttribute("post", postService.postArticle(postId));
        model.addAttribute("files", postService.getFileInfo(postId));
        return "views/post-article";
    }



    /**
     * 새로운 게시물을 작성할 수 있는 폼을 제공하는 뷰를 반환합니다.
     * 뷰(모델)에는 게시물 작성에 필요한 카테고리 목록이 포함됩니다.
     *
     * @param model Spring MVC의 `Model` 객체로, 뷰에 전달할 데이터가 포함됩니다.
     * @return 뷰의 경로와 이름을 문자열로 return 합니다.
     */
    @GetMapping("/newPost")
    public String newPostForm(Model model) {
        model.addAttribute("categories", postService.getCategories());
        return "views/post-create-form";
    }



    /**
     * 지정된 ID의 게시물을 수정할 수 있는 폼을 제공하는 뷰를 반환합니다.
     * 뷰(모델)에는 게시물 수정에 필요한 카테고리 목록과 기존 게시물 데이터(첨부파일 포함)가 포함됩니다.
     *
     * @param model Spring MVC의 `Model` 객체로, 뷰에 전달할 데이터가 포함됩니다.
     * @param postId 게시물의 고유 ID 입니다.
     * @return 뷰의 경로와 이름을 문자열로 return 합니다.
     */
    @GetMapping("/editPost")
    public String editPostForm(Model model, @RequestParam("postId") int postId) {
        model.addAttribute("post", postService.postArticle(postId));
        model.addAttribute("categories", postService.getCategories());
        model.addAttribute("files", postService.getFileInfo(postId));
        return "views/post-edit-form";
    }



    /**
     * 전송된 게시물 데이터와 첨부 파일을 처리하여 데이터베이스에 저장합니다.
     * 작업이 완료되면 게시물 목록 페이지로 리다이렉션합니다.
     *
     * @param postDTO 게시물 작성에 필요한 데이터가 포함된 DTO 객체입니다.
     * @param files 게시물에 첨부된 파일들입니다.
     * @return "redirect:/posts" 게시물 목록 페이지로 리다이렉션합니다.
     */
    @PostMapping("/post")
    public String addPost(@ModelAttribute PostInsertDTO postDTO, @RequestParam("files") MultipartFile[] files) {
        postService.insertPost(postDTO, files);
        return "redirect:/posts";
    }


    /**
     * 전송된 게시물 데이터와 첨부파일을 처리하여 게시물을 수정합니다.
     * 작업이 완료되면 수정한 게시물 상세 페이지로 리다이렉션합니다.
     *
     * @param postDTO 게시물 작성에 필요한 데이터가 포함된 DTO 객체입니다.
     * @param files 게시물에 첨부된 파일들입니다.
     * @return "redirect:/post?postId={postId}" 게시물 상세 페이지로 리다이렉션합니다.
     */
    @PutMapping("/post")
    public String editPost(@ModelAttribute PostUpdateDTO postDTO, @RequestParam("files") MultipartFile[] files) {
        postService.updatePost(postDTO, files);
        return "redirect:/post?postId=" + postDTO.getPostId();
    }



}

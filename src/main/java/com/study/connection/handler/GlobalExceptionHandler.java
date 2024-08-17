package com.study.connection.handler;

import com.study.connection.handler.exception.InvalidPasswordException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPasswordException.class)
    public String handleInvalidPasswordException(InvalidPasswordException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        // return "redirect:/edit-post";
        return "views/error";
    }
}

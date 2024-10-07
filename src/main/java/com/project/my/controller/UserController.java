package com.project.my.controller;

import com.project.my.dto.UserDTO;
import com.project.my.service.UserService;
import com.project.my.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User principal, HttpServletResponse response, Model model) {
        if (principal != null) {
            String email = principal.getAttribute("email");

            // 이메일로 사용자를 조회
            UserDTO userDTO = userService.findByEmail(email)
                    .orElseGet(() -> {
                        // 만약 이메일로 사용자가 없다면 새로 생성하여 저장
                        UserDTO newUserDTO = UserDTO.builder()
                                .username(principal.getAttribute("name"))
                                .email(email)
                                .picture(principal.getAttribute("picture"))
                                .build();
                        return userService.saveOrUpdate(newUserDTO);
                    });

            //userDTO에는 기존 사용자의 id가 포함되어 있거나, 새로 생성된 사용자 정보가 포함됨
            String accessToken = jwtUtil.generateAccessToken(userDTO);
            String refreshToken = jwtUtil.generateRefreshToken(userDTO);

            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setMaxAge((int) (jwtUtil.getAccessTokenValidity() / 1000)); // Access Token 유효 기간 설정, ms->s로 변환하기 위해 1000을 나눔
            accessCookie.setPath("/");

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setMaxAge((int) (jwtUtil.getRefreshTokenValidity() / 1000)); // Refresh Token 유효 기간 설정
            refreshCookie.setPath("/");

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            model.addAttribute("user", userDTO);
        }
        return "home";
    }




    @GetMapping("/refresh-token")
    public String refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null && jwtUtil.validateRefreshToken(refreshToken)) {
            String email = jwtUtil.extractEmail(refreshToken);
            UserDTO userDTO = userService.findByEmail(email).orElse(null);

            if (userDTO != null) {
                String newAccessToken = jwtUtil.generateAccessToken(userDTO);
                Cookie newAccessCookie = new Cookie("accessToken", newAccessToken);
                newAccessCookie.setHttpOnly(true);
                newAccessCookie.setMaxAge(600); // 10 minutes
                newAccessCookie.setPath("/");
                response.addCookie(newAccessCookie);
            }
        }

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // JWT 쿠키 삭제
        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setMaxAge(0);
        accessCookie.setPath("/");

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(0);
        refreshCookie.setPath("/");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        // JSESSIONID 쿠키 삭제
        Cookie jsessionCookie = new Cookie("JSESSIONID", null);
        jsessionCookie.setHttpOnly(true);
        jsessionCookie.setSecure(true);
        jsessionCookie.setMaxAge(0);
        jsessionCookie.setPath("/");
        response.addCookie(jsessionCookie);

        // 세션 무효화
        request.getSession().invalidate();

        return "redirect:/home";
    }
}

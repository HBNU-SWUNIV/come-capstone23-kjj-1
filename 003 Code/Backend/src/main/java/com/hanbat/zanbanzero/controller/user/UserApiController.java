package com.hanbat.zanbanzero.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanbat.zanbanzero.dto.user.info.UserInfoDto;
import com.hanbat.zanbanzero.dto.user.user.UserDto;
import com.hanbat.zanbanzero.dto.user.user.UserMyPageDto;
import com.hanbat.zanbanzero.exception.controller.exceptions.CantFindByIdException;
import com.hanbat.zanbanzero.exception.controller.exceptions.JwtException;
import com.hanbat.zanbanzero.exception.filter.ExceptionTemplate;
import com.hanbat.zanbanzero.exception.controller.exceptions.SameNameException;
import com.hanbat.zanbanzero.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody UserDto dto) throws SameNameException, JsonProcessingException {
        userService.join(dto);

        return ResponseEntity.status(HttpStatus.OK).body("회원가입에 성공했습니다.");
    }

    @PostMapping("/join/check")
    public ResponseEntity<String> check(@RequestBody UserDto dto) {
        boolean result = userService.check(dto);
        if (result) return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 아이디입니다.");
        else return ResponseEntity.ok("사용 가능한 아이디입니다.");
    }

    @GetMapping("/api/user/info")
    public ResponseEntity<UserInfoDto> getInfo(@RequestBody UserDto dto) throws JwtException {
        UserInfoDto user = userService.getInfo(dto);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/api/user/{id}/page")
    public ResponseEntity<UserMyPageDto> getMyPage(@PathVariable Long id) throws CantFindByIdException, JsonProcessingException {
        UserMyPageDto userMyPageDto = userService.getMyPage(id);

        return ResponseEntity.status(HttpStatus.OK).body(userMyPageDto);
    }

}
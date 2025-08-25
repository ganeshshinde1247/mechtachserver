package com.mechtech.automach.controller;

import com.mechtech.automach.constant.AppAPIConstant;
import com.mechtech.automach.dto.UserCredentialsDTO;
import com.mechtech.automach.enums.OperationStatus;
import com.mechtech.automach.entity.User;
import com.mechtech.automach.models.HttpResult;
import com.mechtech.automach.models.ResponseViewModel;
import com.mechtech.automach.service.AuthService;
import com.mechtech.automach.service.UserLoginDetailsService;
import com.mechtech.automach.service.getUsersDataservice;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class userController {

    private final getUsersDataservice userService;
    private final UserLoginDetailsService userLoginDetailsService;
    private final AuthService authService;

    @Autowired
    public userController(getUsersDataservice userService, UserLoginDetailsService userLoginDetailsService, AuthService authService) {
        this.userService = userService;
        this.userLoginDetailsService = userLoginDetailsService;
        this.authService = authService;
    }

   /*  @PostMapping("/userDetails")
    public ResponseEntity<User> getUserDetails(@RequestBody User user) {
        User userData = userService.getUserData(user.getUsername(), user.getUserpassword());
        if (userData != null) {
            return ResponseEntity.ok(userData);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/

    @PostMapping(AppAPIConstant.VALIDATE_USER_LOGIN_DETAILS)
    public ResponseViewModel validateUserLoginDetails(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> userLoginDetails = userLoginDetailsService.validateUserDetails(requestData);

        if (userLoginDetails.containsKey("error")) {
            String messageToClient = userLoginDetails.get("error").toString();
            return new ResponseViewModel("", new HttpResult(
                    HttpStatus.OK.value(), messageToClient,
                    OperationStatus.Error.value(), ""));
        } else {
            return new ResponseViewModel(userLoginDetails.get("UserLoginDetails"), new HttpResult(
                    HttpStatus.OK.value(), "",
                    OperationStatus.Success.value(), ""));
        }
    }

    @PostMapping("/login")
    public ResponseViewModel login(@RequestBody UserCredentialsDTO credentials, HttpServletResponse response) {
        List<String> errorMessage = new ArrayList<>();
        Map<String, Object> authResponse = authService.login(credentials, response, errorMessage);

        if (!errorMessage.isEmpty()) {
            return new ResponseViewModel("", new HttpResult(
                    HttpStatus.UNAUTHORIZED.value(), String.join(", ", errorMessage),
                    OperationStatus.Error.value(), ""));
        } else {
            return new ResponseViewModel(authResponse, new HttpResult(
                    HttpStatus.OK.value(), "Login successful",
                    OperationStatus.Success.value(), ""));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseViewModel refreshToken(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        List<String> errorMessage = new ArrayList<>();
        Map<String, Object> refreshResponse = authService.refreshToken(token, response, errorMessage);

        if (!errorMessage.isEmpty()) {
            return new ResponseViewModel("", new HttpResult(
                    HttpStatus.UNAUTHORIZED.value(), String.join(", ", errorMessage),
                    OperationStatus.Error.value(), ""));
        } else {
            return new ResponseViewModel(refreshResponse, new HttpResult(
                    HttpStatus.OK.value(), "Token refreshed successfully",
                    OperationStatus.Success.value(), ""));
        }
    }
}

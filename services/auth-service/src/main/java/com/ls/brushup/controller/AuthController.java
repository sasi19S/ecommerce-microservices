package com.ls.brushup.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ls.brushup.util.JwtUtil;
import com.ls.brushup.model.User;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User user) {
    try {
        System.out.println("AuthController: login");
        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(token);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
}
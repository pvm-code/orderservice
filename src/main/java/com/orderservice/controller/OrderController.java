package com.orderservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/v1/order")
public class OrderController {
	
	

    @GetMapping("/welcome")
    public ResponseEntity<String> profile(
            ) {

        return ResponseEntity.ok(
                "Welcome "
        );
    }





}

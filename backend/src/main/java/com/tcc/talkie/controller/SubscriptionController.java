package com.tcc.talkie.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcc.talkie.dto.response.ApiResponse;
import com.tcc.talkie.dto.response.SubscriptionResponseDTO;
import com.tcc.talkie.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService service;

    @PostMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<SubscriptionResponseDTO>> subscribe(@PathVariable Long categoryId) {
        var result = service.subscribe(categoryId);
        return ResponseEntity.ok(new ApiResponse<>("Inscrito com sucesso", result));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(@PathVariable Long categoryId) {
        service.unsubscribe(categoryId);
        return ResponseEntity.ok(new ApiResponse<>("Inscrição removida com sucesso", null));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<SubscriptionResponseDTO>>> listMine() {
        var result = service.listMySubscriptions();
        return ResponseEntity.ok(new ApiResponse<>("Suas inscrições recuperadas com sucesso", result));
    }
}

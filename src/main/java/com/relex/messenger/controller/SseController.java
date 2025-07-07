package com.relex.messenger.controller;

import com.relex.messenger.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping("/access-token")
    public SseEmitter pushToken(@RequestParam String email) {
        return sseService.addEmitter(email);
    }
}

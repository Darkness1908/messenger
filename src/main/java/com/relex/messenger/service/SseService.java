package com.relex.messenger.service;

import com.relex.messenger.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public SseEmitter addEmitter(String email) {
        SseEmitter emitter = new SseEmitter(300_000L);
        emitters.put(email, emitter);
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));
        return emitter;
    }

    public void pushToken(String accessToken) throws IOException {
        String email = userRepository.getEmailById(jwtService.extractUserId(accessToken));
        SseEmitter emitter = emitters.get(email);
        if (emitter != null) {
            Map<String, String> payload = Map.of("accessToken", accessToken);
            emitter.send(SseEmitter.event().data(payload));
            emitter.complete();
            emitters.remove(email);
        }
    }

}

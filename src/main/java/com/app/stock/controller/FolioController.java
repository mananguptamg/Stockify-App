package com.app.stock.controller;

import com.app.stock.dto.FolioItemDTO;
import com.app.stock.service.EmailService;
import com.app.stock.service.FolioService;
import com.app.stock.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/folio")
@RequiredArgsConstructor
public class FolioController {
    private final FolioService folioService;
    private final AuthUtil authUtil;
    private final EmailService emailService;

    @PostMapping("/upload")
    public ResponseEntity<?> addFolioItem(@RequestBody FolioItemDTO request) {
        try {
            folioService.addFolioItem(request, authUtil.getCurrentUser());
            return ResponseEntity.ok("Stock added successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add stock: " + e.getMessage());
        }
    }

    @GetMapping("/value")
    public ResponseEntity<?> getFolioValue() {
        var user = authUtil.getCurrentUser();
        var response = folioService.getFolioValue(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/email")
    public ResponseEntity<?> emailFolio() {
        try {
            var user = authUtil.getCurrentUser();
            var folio = folioService.getFolioValue(user);
            emailService.sendFolioEmail(user, folio);
            return ResponseEntity.ok("Folio emailed to " + user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email: " + e.getMessage());
        }
    }
}

package com.divyesh.panchasara.cp_forge.controller;

import com.divyesh.panchasara.cp_forge.client.CodeforcesApiClient;
import com.divyesh.panchasara.cp_forge.dto.CodeforcesAddRequest;
import com.divyesh.panchasara.cp_forge.dto.CodeforcesProfileDto;
import com.divyesh.panchasara.cp_forge.service.CodeforcesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/codeforces")
public class CodeforcesController {

    private final CodeforcesService codeforcesService;

    public CodeforcesController(CodeforcesService codeforcesService) {
        this.codeforcesService = codeforcesService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<CodeforcesProfileDto> addProfile(@Valid @RequestBody CodeforcesAddRequest request) {
        try {
            CodeforcesProfileDto dto = codeforcesService.addOrRefreshProfile(request.handle());
            return ResponseEntity.ok(dto);
        } catch (CodeforcesApiClient.CodeforcesApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profiles")
    public ResponseEntity<List<CodeforcesProfileDto>> listProfiles() {
        return ResponseEntity.ok(codeforcesService.listProfiles());
    }

    @GetMapping("/profiles/{handle}")
    public ResponseEntity<CodeforcesProfileDto> getProfile(@PathVariable String handle) {
        try {
            return ResponseEntity.ok(codeforcesService.getProfile(handle));
        } catch (CodeforcesService.CodeforcesNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profiles/{handle}/refresh")
    public ResponseEntity<CodeforcesProfileDto> refreshProfile(@PathVariable String handle) {
        try {
            CodeforcesProfileDto dto = codeforcesService.refreshProfile(handle);
            return ResponseEntity.ok(dto);
        } catch (CodeforcesApiClient.CodeforcesApiException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (CodeforcesService.CodeforcesNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

package com.professionalpractice.medicalbookingbespring.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.professionalpractice.medicalbookingbespring.config.RestApiV1;
import com.professionalpractice.medicalbookingbespring.dtos.HealthFormDTO;
import com.professionalpractice.medicalbookingbespring.dtos.request.HealthFormRequest;
import com.professionalpractice.medicalbookingbespring.dtos.response.PaginationResponse;
import com.professionalpractice.medicalbookingbespring.services.CloudinaryService;
import com.professionalpractice.medicalbookingbespring.services.HealthFormService;
import com.professionalpractice.medicalbookingbespring.utils.CustomResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestApiV1
public class HealthFormController {

    private final HealthFormService healthFormService;

    private final CloudinaryService cloudinaryService;

    @PostMapping("/health-forms")
    public ResponseEntity<?> createHealthForms(
            @ModelAttribute HealthFormRequest healthFormRequest,
            @RequestParam(value = "cccd") MultipartFile cccdFile,
            @RequestParam(value = "bhyt", required = false) MultipartFile bhytFile) {

        String ccdUrl = cloudinaryService.uploadImage(cccdFile);
        healthFormRequest.setCccdUrl(ccdUrl);
        if (bhytFile != null) {
            String bhytUrl = cloudinaryService.uploadImage(bhytFile);
            healthFormRequest.setBhytUrl(bhytUrl);
        }

        HealthFormDTO healthForm = healthFormService.createHealthForm(healthFormRequest);
        return CustomResponse.success(HttpStatus.CREATED, "Toạ đơn khám thành công", healthForm);
    }

    @GetMapping("/health-forms")
    public ResponseEntity<?> getHealthForms(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int limit,
                                            @RequestParam(required = false) Integer status) {
        PageRequest pageRequest = PageRequest.of(
                page - 1, limit,
                Sort.by("id").ascending());
        Page<HealthFormDTO> healthFormPage;
        if(status != null) {
            healthFormPage = healthFormService.getHealthFormByStatus(status ,pageRequest);
        } else {
            healthFormPage = healthFormService.getHealthForms(pageRequest);
        }

        long totalPages = healthFormPage.getTotalElements();
        List<HealthFormDTO> healthForms = healthFormPage.getContent();
        return CustomResponse.success(new PaginationResponse(page, limit, totalPages, healthForms));
    }


    @GetMapping("/health-forms/history")
    public ResponseEntity<?> getHealthFormsByUserId(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        PageRequest pageRequest = PageRequest.of(
                page - 1, limit,
                Sort.by("id").ascending());

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Page<HealthFormDTO> healthFormPage = healthFormService.getHistory(userEmail, pageRequest);

        long totalPages = healthFormPage.getTotalElements();
        List<HealthFormDTO> healthForms = healthFormPage.getContent();

        return CustomResponse.success(new PaginationResponse(page, limit, totalPages, healthForms));
    }

    @PutMapping("/health-forms/{healthFormId}")
    public ResponseEntity<?> updateHealthForm(@PathVariable Long healthFormId,
            @RequestBody HealthFormRequest healthFormRequest) {
        HealthFormDTO healthForm = healthFormService.updateHealthForm(healthFormId, healthFormRequest);
        return CustomResponse.success("Cập nhật thành công");
    }

    @DeleteMapping("/health-forms/{healthFormId}")
    public ResponseEntity<?> deleteHealthForm(@PathVariable Long healthFormId) {
        healthFormService.deleteHealthFormById(healthFormId);
        return CustomResponse.success("Xóa thành công");
    }

    @PatchMapping("/health-forms/{healthFormId}")
    public ResponseEntity<?> aproveHealthForm(@PathVariable Long healthFormId,
                                              @RequestBody HealthFormRequest healthFormRequest) {
        HealthFormDTO healthForm = healthFormService.updateHealthForm(healthFormId, healthFormRequest);
        return CustomResponse.success("Cập nhật trạng thái thành công");
    }

}

package com.professionalpractice.medicalbookingbespring.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.professionalpractice.medicalbookingbespring.dtos.DoctorDTO;
import com.professionalpractice.medicalbookingbespring.dtos.request.DoctorRequest;
import com.professionalpractice.medicalbookingbespring.entities.Department;
import com.professionalpractice.medicalbookingbespring.entities.Doctor;
import com.professionalpractice.medicalbookingbespring.exceptions.NotFoundException;
import com.professionalpractice.medicalbookingbespring.repositories.DepartmentRepository;
import com.professionalpractice.medicalbookingbespring.repositories.DoctorRepository;
import com.professionalpractice.medicalbookingbespring.services.DoctorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    private final DepartmentRepository departmentRepository;

    private final ModelMapper modelMapper;

    @Override
    public DoctorDTO createDoctor(DoctorRequest doctorRequest) {
        Department existingDepartment = departmentRepository.findById(doctorRequest.getDepartment())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chuyên khoa này"));

        String imageUrl = doctorRequest.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = "https://cdn-icons-png.flaticon.com/512/3774/3774299.png";
        }

        Doctor doctor = Doctor.builder()
                .name(doctorRequest.getName())
                .degree(doctorRequest.getDegree())
                .experience(doctorRequest.getExperience())
                .image(imageUrl)
                .description(doctorRequest.getDescription())
                .department(existingDepartment)
                .build();

        Doctor saveDoctor = doctorRepository.save(doctor);
        return modelMapper.map(saveDoctor, DoctorDTO.class);
    }

    @Override
    public Page<DoctorDTO> getDoctors(PageRequest pageRequest) {
        Page<Doctor> doctorPage = doctorRepository.queryDoctors(pageRequest);
        return doctorPage.map(doctor -> modelMapper.map(doctor, DoctorDTO.class));
    }

    @Override
    public DoctorDTO updateDoctor(Long doctorId, DoctorRequest doctorRequest) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bác sĩ này!"));
        Department department = departmentRepository.findById(doctorRequest.getDepartment())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy chuyên khoa này!"));

        String imageUrl = doctorRequest.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageUrl = doctor.getImage();
        }

        doctor.setName(doctorRequest.getName());
        doctor.setDepartment(department);
        doctor.setImage(imageUrl);
        doctor.setDegree(doctorRequest.getDegree());
        doctor.setExperience(doctorRequest.getExperience());
        doctor.setDescription(doctorRequest.getDescription());

        Doctor saveDoctor = doctorRepository.save(doctor);
        return modelMapper.map(saveDoctor, DoctorDTO.class);
    }

    @Override
    public void deleteDoctor(Long doctorId) {
        doctorRepository.deleteById(doctorId);
    }
}

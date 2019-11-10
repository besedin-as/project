package com.example.project.repos;

import com.example.project.domain.UploadFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UploadFileRepo extends CrudRepository<UploadFile, Long> {

    List<UploadFile> findByFilePathContaining(String filePath);
}

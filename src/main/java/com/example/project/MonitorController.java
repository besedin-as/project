package com.example.project;

import com.example.project.domain.UploadFile;
import com.example.project.repos.UploadFileRepo;
import org.apache.logging.log4j.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class MonitorController {

    @Autowired
    private UploadFileRepo uploadFileRepo;

    private static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/files";

    @PostMapping("/upload")
    public String uploading(@RequestParam(name="files") MultipartFile file, Map<String, Object> model) {
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        UploadFile uploadFile = new UploadFile(fileNameAndPath.toAbsolutePath().toString());
        uploadFileRepo.deleteAll();
        uploadFileRepo.save(uploadFile);
        return "redirect:/"+file.getOriginalFilename();
    }

    @GetMapping("/upload")
    public String home(Map<String, Object> model) {
        return "file_upload";
    }

    @GetMapping("/{uploadFile}")
    public HttpEntity<byte[]> getFile(@PathVariable("uploadFile") String fileName) throws IOException {
        String filePath = uploadFileRepo.findByFilePathContaining(fileName).get(0).getFilePath();
        byte[] document = FileCopyUtils.copyToByteArray(new File(filePath));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "pdf"));
        header.set("Content-Disposition", "inline; filename=" + fileName);
        header.setContentLength(document.length);
        return new HttpEntity<byte[]>(document, header);
    }

    @GetMapping("/")
    public String getFile() throws IOException, URISyntaxException {
        String filePath = uploadFileRepo.findAll().iterator().next().getFilePath();
//        return "redirect:/"+filePath.substring(filePath.lastIndexOf("\\")+1);
        return "monitor";
    }

}

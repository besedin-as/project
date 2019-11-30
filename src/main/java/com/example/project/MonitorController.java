package com.example.project;

import com.example.project.domain.FilePosition;
import com.example.project.repos.FilePositionRepo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Controller
public class MonitorController {

    @Autowired
    private FilePositionRepo filePositionRepo;

    private static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/files";
    private static String templatesDirectory = System.getProperty("user.dir") + "/src/main/resources/templates";

    @PostMapping("/upload")
    public String uploading(@RequestParam(name="files") MultipartFile file, Map<String, Object> model) throws IOException, ParserConfigurationException {
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilePosition uploadFile = new FilePosition(0, 0);
        filePositionRepo.deleteAll();
        filePositionRepo.save(uploadFile);
//        generateHTMLFromPDF(fileNameAndPath.toString());
        return "redirect:/file";
    }

    @GetMapping("/upload")
    public String home(Map<String, Object> model) {
        return "file_upload";
    }

    @GetMapping("/file")
    public String getFile(Map<String, Object> model) {
        return "test";
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void setScrollTop(@RequestParam int scrollTop) {
        FilePosition uploadFile = filePositionRepo.findAll().iterator().next();
        uploadFile.setScrollTop(scrollTop);
        filePositionRepo.save(uploadFile);
    }

    @RequestMapping(value = "/file/update_position", method = RequestMethod.GET)
    public ResponseEntity<Integer> getScrollPosition() {
        FilePosition uploadFile = filePositionRepo.findAll().iterator().next();
        return ResponseEntity.ok(uploadFile.getScrollTop());

    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    private void generateHTMLFromPDF(String filename) throws IOException, ParserConfigurationException {
        PDDocument pdf = PDDocument.load(new File(filename));
        Writer output = new PrintWriter(templatesDirectory+"/test.mustache", "utf-8");
        new PDFDomTree().writeText(pdf, output);

        output.close();
    }
}

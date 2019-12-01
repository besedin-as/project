package com.example.project;

import com.example.project.domain.FilePosition;
import com.example.project.repos.FilePositionRepo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

@Controller
public class MonitorController {

    @Autowired
    private FilePositionRepo filePositionRepo;

    private static String uploadDirectory = System.getProperty("user.dir") + "/src/main/resources/static/files";
    private static String templatesDirectory = System.getProperty("user.dir") + "/src/main/resources/templates";

    @PostMapping("/upload")
    public String uploading(@RequestParam(name = "files") MultipartFile file, Map<String, Object> model) throws IOException, ParserConfigurationException {
        Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
        try {
            Files.write(fileNameAndPath, file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FilePosition uploadFile = new FilePosition(0, 0);
        filePositionRepo.deleteAll();
        filePositionRepo.save(uploadFile);
        Random random = new Random();
        generateHTMLFromPDF(fileNameAndPath.toString());
        String text = readLineByLineJava(templatesDirectory + "/test.html");
        text = insert(text);
        writeToFile(templatesDirectory + "/test.html", text);
        return "redirect:/file";
    }

    @GetMapping("/upload")
    public String home(Map<String, Object> model) {
        return "file_upload";
    }

//    @GetMapping("/file")
//    public String getFile(Map<String, Object> model) {
//        return "test";
//    }

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

    @GetMapping("/file")
    public HttpEntity<byte[]> uploadFile() throws IOException {
        byte[] document = FileCopyUtils.copyToByteArray(new File(templatesDirectory + "/test.html"));
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("text", "html"));
        header.set("Content-Disposition", "inline; filename=test.html");
        header.setContentLength(document.length);
        return new HttpEntity<byte[]>(document, header);
    }

    private void generateHTMLFromPDF(String filename) throws IOException, ParserConfigurationException {
        PDDocument pdf = PDDocument.load(new File(filename));
        Writer output = new PrintWriter(templatesDirectory + "/test.html", "utf-8");
        new PDFDomTree().writeText(pdf, output);
        output.close();
    }

    private String readLineByLineJava(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    private String insert(String text) {
        String insertText = "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js\" type=\"text/javascript\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "$(document).ready(function () {\n" +
                "\n" +
                "    window.onload = function () {\n" +
                "        window.onscroll = function () {\n" +
                "            var scrollTop = window.pageYOffset || document.documentElement.scrollTop;\n" +
                "            $.ajax({\n" +
                "                url: \"/file\",\n" +
                "                type: \"POST\",\n" +
                "                data: {\n" +
                "                    scrollTop: scrollTop\n" +
                "                },\n" +
                "                success: function () {\n" +
                "                    return true;\n" +
                "                }\n" +
                "            })\n" +
                "        };\n" +
                "    };\n" +
                "\n" +
                "    setInterval(function () {\n" +
                "        $.ajax({\n" +
                "            url: \"/file/update_position\",\n" +
                "            type: \"GET\",\n" +
                "            success: function (data) {\n" +
                "                window.scrollTo(0, data);\n" +
                "            }\n" +
                "        })\n" +
                "    }, 1000);\n" +
                "});\n" +
                "</script>\n";
        text = text.replace("</body>", insertText + "</body>");
        return text;
    }

    private void writeToFile(String filePath, String text)
            throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write(text);
        writer.close();
    }
}

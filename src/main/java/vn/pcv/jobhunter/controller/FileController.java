package vn.pcv.jobhunter.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.pcv.jobhunter.domain.dto.file.ReponseUploadFile;
import vn.pcv.jobhunter.service.FileService;
import vn.pcv.jobhunter.util.error.UploadExceptiion;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${pcv.upload-file.base-uri}")
    private String baseUri;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;

    }

    @PostMapping("/files")
    public ResponseEntity<ReponseUploadFile> upload(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder) throws URISyntaxException, IOException, UploadExceptiion {

        if (file == null || file.isEmpty()) {
            throw new UploadExceptiion("Bạn không chuyền file lên param");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new UploadExceptiion("Không được lưu file " + allowedExtensions.toString());
        }

        this.fileService.creatDirectory(folder);

        ReponseUploadFile res = new ReponseUploadFile();
        res.setUploadedAt(Instant.now());
        res.
        setFileName(this.fileService.store(file, folder));
        return ResponseEntity.ok().body(res);

    }

    @GetMapping("/files")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws UploadExceptiion, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new UploadExceptiion("Missing required params : (fileName or folder) in query params.");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new UploadExceptiion("File with name = " + fileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

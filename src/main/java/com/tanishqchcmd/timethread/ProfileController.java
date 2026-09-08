package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class ProfileController {

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/profile/upload")
    public String uploadProfilePicture(Authentication authentication, @RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) return "redirect:/dashboard";

        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();

        try {
            // 1. Ensure the uploads directory exists
            Path uploadPath = Paths.get(UPLOAD_DIRECTORY);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generate a unique file name so users don't overwrite each other's "avatar.png"
            String fileName = student.getId() + "_" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 3. Save the actual file to the hard drive
            Files.write(filePath, file.getBytes());

            // 4. Save the file name to the database
            student.setProfilePicture(fileName);
            studentRepository.save(student);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/dashboard?clear=true";
    }
}
package GPT.controller;

import GPT.dto.ResponseDto;
import GPT.service.ImageInputService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/response")
public class GptController {
    private  final ImageInputService imageInputService;

    public GptController(ImageInputService imageInputService) {
        this.imageInputService = imageInputService;
    }


    @PostMapping("/image/sonnet")
    public ResponseEntity<ResponseDto<Object>> createSonnetResponse(@RequestParam int promptId,
                                                                   @RequestParam MultipartFile  writingImage,
                                                                   HttpServletRequest request) throws IOException {
        return imageInputService.createResponseByImage(promptId, writingImage, request);
    }

    @PostMapping("/image/haiku")
    public ResponseEntity<ResponseDto<Object>> createHaikuResponse(@RequestParam int promptId,
                                                                   @RequestParam MultipartFile  writingImage,
                                                                   HttpServletRequest request) throws IOException {
        return imageInputService.createHaikuResponseByImage(promptId, writingImage, request);
    }

}


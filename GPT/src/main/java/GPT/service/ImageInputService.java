package GPT.service;

import GPT.dto.ResponseDto;
import GPT.entity.AiPrompt;
import GPT.repository.AiPromptRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ImageInputService {

    private static final Logger log = LogManager.getLogger(ImageInputService.class);
    private final AnthropicChatModel anthropicChatModel;
    private final AnthropicChatModel anthropicChatHaikuModel;
    private final OpenAiChatModel openAiChatModel;
    private final AiPromptRepository aiPromptRepository;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public ImageInputService(@Qualifier("anthropicChatModel")AnthropicChatModel anthropicChatModel, @Qualifier("anthropicChatHaikuModel") AnthropicChatModel anthropicChatHaikuModel, OpenAiChatModel openAiChatModel, AiPromptRepository aiPromptRepository) {
        this.anthropicChatModel = anthropicChatModel;
        this.anthropicChatHaikuModel = anthropicChatHaikuModel;
        this.openAiChatModel = openAiChatModel;
        this.aiPromptRepository = aiPromptRepository;
    }

    public ResponseEntity<ResponseDto<Object>> createResponseByImage(int promptId, MultipartFile writingImg, HttpServletRequest request) {
        try {
            AiPrompt aiPrompt = aiPromptRepository.findById(promptId)
                    .orElseThrow(() -> new IllegalArgumentException("프롬프트가 존재하지 않습니다."));

            String chatResponse = getClaudeChatResponse(writingImg, aiPrompt, this.anthropicChatModel);
            Map<String, Object> jsonData = toMap(chatResponse);
            if (jsonData == null) {
                //gpt 모델은 jsonStrict 버전으로 json 형식으로 무조건 응답
                chatResponse = getGptChatResponse(writingImg, aiPrompt);
                jsonData = toMap(chatResponse);
            }

            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "레포트 출력 완료", jsonData));

        } catch (IllegalArgumentException e) {
            log.error("claudeWrtReportTest error : ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "프롬프트가 존재하지 않습니다.", null));
        }
        catch (Exception e) {
            log.error("claudeWrtReportTest error : " , e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다",null));
        }
    }

    public ResponseEntity<ResponseDto<Object>> createHaikuResponseByImage(int promptId, MultipartFile writingImg, HttpServletRequest request) {
        try {
            AiPrompt aiPrompt = aiPromptRepository.findById(promptId)
                    .orElseThrow(() -> new IllegalArgumentException("프롬프트가 존재하지 않습니다."));

            String chatResponse = getClaudeChatResponse(writingImg, aiPrompt, this.anthropicChatHaikuModel);
            Map<String, Object> jsonData = toMap(chatResponse);
            if (jsonData == null) {
                //gpt 모델은 jsonStrict 버전으로 json 형식으로 무조건 응답
                chatResponse = getGptChatResponse(writingImg, aiPrompt);
                jsonData = toMap(chatResponse);
            }

            return ResponseEntity.ok(new ResponseDto<>(HttpStatus.OK.value(), "레포트 출력 완료", jsonData));

        } catch (IllegalArgumentException e) {
            log.error("claudeWrtReportTest error : ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), "프롬프트가 존재하지 않습니다.", null));
        }
        catch (Exception e) {
            log.error("claudeWrtReportTest error : " , e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body(new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "알 수 없는 오류가 발생했습니다",null));
        }
    }


    public String getClaudeChatResponse(MultipartFile writingImg, AiPrompt aiPrompt, AnthropicChatModel chatModel) throws IOException {
        try {
            String systemPromptClaude = aiPrompt.getSystemPromptClaude();
            String outputPrompt = aiPrompt.getOutputPromptClaude();
            SystemMessage system = new SystemMessage(systemPromptClaude);

            ByteArrayResource resource = toResource(writingImg);

            UserMessage userMessage_image = UserMessage.builder()
                    .text(outputPrompt)
                    .media(List.of(new Media(MimeTypeUtils.IMAGE_JPEG, resource)))
                    .build();

            List<Message> prompts = List.of(system, userMessage_image);
            ChatResponse chatResponse = chatModel.call(
                    new Prompt(prompts));

            return chatResponse.getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("getClaudeChatResponse error : ", e);
            return null;
        }
    }

    public String getGptChatResponse(MultipartFile writingImg, AiPrompt aiPrompt) throws IOException {
        try {
            String systemPromptGpt = aiPrompt.getSystemPromptGpt();
            String outputPrompt = aiPrompt.getOutputPromptGpt();
            SystemMessage system = new SystemMessage(systemPromptGpt);

            ByteArrayResource resource = toResource(writingImg);
            UserMessage userMessage_image = UserMessage.builder()
                    .text("")
                    .media(List.of(new Media(MimeTypeUtils.IMAGE_JPEG, resource)))
                    .build();

            List<Message> prompts = List.of(system, userMessage_image);

            ChatResponse chatResponse = this.openAiChatModel.call(
                    new Prompt((prompts),
                            OpenAiChatOptions.builder()
                                    .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_SCHEMA, outputPrompt)).build()));
            return chatResponse.getResult().getOutput().getText();
        } catch (Exception e) {
            log.error("getGptChatResponse error : ", e);
            return null;
        }
    }

    private Map<String, Object> toMap(String text) {
        if (text == null) return null;

        text = text.trim();
        // JSON 객체 형태 여부 확인
        if (!text.startsWith("{") || !text.endsWith("}")) return null;

        try {
            return objectMapper.readValue(text, Map.class);
        } catch (Exception e) {
            return null;
        }
    }

    private ByteArrayResource toResource(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getInputStream().readAllBytes());
    }

}

package GPT.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {
    public final String openAiApi;

    public OpenAiConfig(@Value("${spring.ai.openai.api-key}") String openAiApi) {
        this.openAiApi = openAiApi;
    }

    @Bean
    public OpenAiApi openAiApi() {
        return new OpenAiApi.Builder().apiKey(openAiApi).build();
    }

    @Bean
    public OpenAiChatOptions openAiChatOptions() {
        return new OpenAiChatOptions.Builder().model("gpt-4o").temperature(0.3).maxTokens(5000).build();
    }

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder().openAiApi(this.openAiApi()).defaultOptions(this.openAiChatOptions()).build();
    }
}

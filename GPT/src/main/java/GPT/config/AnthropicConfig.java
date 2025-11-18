package GPT.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AnthropicConfig {

    public final String anthropicWrtApi;

    public AnthropicConfig(@Value("${anthropic.api.key}") String anthropicWrtApi) {
        this.anthropicWrtApi = anthropicWrtApi;
    }


    @Bean
    public AnthropicApi anthropicWrtApi() {
        return new AnthropicApi.Builder().
                apiKey(anthropicWrtApi)
                .build();
    }

    @Bean
    public AnthropicChatOptions anthropicChatOptions() {
        return new AnthropicChatOptions.Builder()
                .model("claude-3-7-sonnet-20250219")
                .temperature(0.3)
                .maxTokens(5000)
                .build();
    }

    @Bean AnthropicChatOptions anthropicChatOptionsHaiku() {
        return new AnthropicChatOptions.Builder()
                .model("claude-haiku-4-5-20251001")
                .temperature(0.3)
                .maxTokens(5000)
                .build();
    }

    @Bean(name = "anthropicChatModel")
    public AnthropicChatModel anthropicWrtChatModel() {
        return AnthropicChatModel.builder().anthropicApi(this.anthropicWrtApi()).defaultOptions(this.anthropicChatOptions()).build();
    }

    @Bean(name = "anthropicChatHaikuModel")
    public AnthropicChatModel anthropicWrtChatHaikuModel() {
        return AnthropicChatModel.builder().anthropicApi(this.anthropicWrtApi()).defaultOptions(this.anthropicChatOptionsHaiku()).build();
    }
}

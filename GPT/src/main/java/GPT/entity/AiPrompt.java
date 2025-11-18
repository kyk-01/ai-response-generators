package GPT.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tpl_ai_prompt")
public class AiPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer promptId;

    private String systemPromptClaude;
    private String systemPromptGpt;
    private String outputPromptClaude;
    private String outputPromptGpt;

    public AiPrompt() {}

    public AiPrompt(Integer promptId, String systemPromptClaude, String systemPromptGpt, String outputPromptClaude, String outputPromptGpt) {
        this.promptId = promptId;
        this.systemPromptClaude = systemPromptClaude;
        this.systemPromptGpt = systemPromptGpt;
        this.outputPromptClaude = outputPromptClaude;
        this.outputPromptGpt = outputPromptGpt;
    }

    public Integer getPromptId() {
        return promptId;
    }

    public void setPromptId(Integer promptId) {
        this.promptId = promptId;
    }

    public String getSystemPromptClaude() {
        return systemPromptClaude;
    }

    public void setSystemPromptClaude(String systemPromptClaude) {
        this.systemPromptClaude = systemPromptClaude;
    }

    public String getSystemPromptGpt() {
        return systemPromptGpt;
    }

    public void setSystemPromptGpt(String systemPromptGpt) {
        this.systemPromptGpt = systemPromptGpt;
    }

    public String getOutputPromptClaude() {
        return outputPromptClaude;
    }

    public void setOutputPromptClaude(String outputPromptClaude) {
        this.outputPromptClaude = outputPromptClaude;
    }

    public String getOutputPromptGpt() {
        return outputPromptGpt;
    }

    public void setOutputPromptGpt(String outputPromptGpt) {
        this.outputPromptGpt = outputPromptGpt;
    }
}

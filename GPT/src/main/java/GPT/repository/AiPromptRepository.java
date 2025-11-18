package GPT.repository;

import GPT.entity.AiPrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiPromptRepository extends JpaRepository<AiPrompt, Integer> {
}

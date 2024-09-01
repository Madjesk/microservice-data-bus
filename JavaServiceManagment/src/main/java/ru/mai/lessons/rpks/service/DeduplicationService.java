package ru.mai.lessons.rpks.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mai.lessons.rpks.model.Deduplication;
import ru.mai.lessons.rpks.repository.DeduplicationRepository;

@Service
@Getter
@NoArgsConstructor
public class DeduplicationService {
    @Autowired
    private DeduplicationRepository deduplicationRepository;

    public Iterable<Deduplication> getAllDeduplicationsByDeduplicationId(long id) {
        return deduplicationRepository.findAllDeduplicationsByDeduplicationId(id);
    }

    public Deduplication getDeduplicationByDeduplicationIdAndRuleId(long deduplicationId, long ruleId) {
        return deduplicationRepository.findDeduplicationByDeduplicationIdAndRuleId(deduplicationId, ruleId);
    }

    public void deleteDeduplicationByDeduplicationIdAndRuleId(long deduplicationId, long ruleId) {
        deduplicationRepository.deleteDeduplicationByDeduplicationIdAndRuleId(deduplicationId, ruleId);
    }

    public Iterable<Deduplication> getAllDeduplications() {
        return deduplicationRepository.findAll();
    }

    public void deleteAll() {
        deduplicationRepository.deleteAll();
    }

    public void save(Deduplication deduplication) {
        deduplicationRepository.save(deduplication);
    }
}
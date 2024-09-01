package ru.mai.lessons.rpks.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mai.lessons.rpks.model.Enrichment;
import ru.mai.lessons.rpks.repository.EnrichmentRepository;

@Service
@Getter
@NoArgsConstructor
public class EnrichmentService {
    @Autowired
    private EnrichmentRepository enrichmentRepository;

    public Iterable<Enrichment> getAllEnrichmentsByEnrichmentId(long id) {
        return enrichmentRepository.findAllEnrichmentsByEnrichmentId(id);
    }

    public Enrichment getEnrichmentByEnrichmentIdAndRuleId(long enrichmentId, long ruleId) {
        return enrichmentRepository.findEnrichmentByEnrichmentIdAndRuleId(enrichmentId, ruleId);
    }

    public void deleteEnrichmentByEnrichmentIdAndRuleId(long enrichmentId, long ruleId) {
        enrichmentRepository.deleteEnrichmentByEnrichmentIdAndRuleId(enrichmentId, ruleId);
    }

    public Iterable<Enrichment> getAllEnrichments() {
        return enrichmentRepository.findAll();
    }

    public void deleteAll() {
        enrichmentRepository.deleteAll();
    }

    public void save(Enrichment enrichment) {
        enrichmentRepository.save(enrichment);
    }
}
package ru.mai.lessons.rpks.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.mai.lessons.rpks.model.Enrichment;

import java.util.List;

@Repository
public interface EnrichmentRepository extends CrudRepository<Enrichment, Long> {
    List<Enrichment> findAllEnrichmentsByEnrichmentId(long id);
    Enrichment findEnrichmentByEnrichmentIdAndRuleId(long enrichmentId, long ruleId);
    void deleteEnrichmentByEnrichmentIdAndRuleId(long enrichmentId, long ruleId);
}

package ru.mai.lessons.rpks.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mai.lessons.rpks.model.Filter;
import ru.mai.lessons.rpks.repository.FilterRepository;

@Service
@Getter
@NoArgsConstructor
public class FilterService {
    @Autowired
    private FilterRepository filterRepository;

    public Iterable<Filter> getAllFiltersByFilterId(long id) {
        return filterRepository.findAllFiltersByFilterId(id);
    }

    public Filter getFilterByFilterIdAndRuleId(long filterId, long ruleId) {
        return filterRepository.findFilterByFilterIdAndRuleId(filterId, ruleId);
    }

    public void deleteFilterByFilterIdAndRuleId(long filterId, long ruleId) {
        filterRepository.deleteFilterByFilterIdAndRuleId(filterId, ruleId);
    }

    public Iterable<Filter> getAllFilters() {
        return filterRepository.findAll();
    }

    public void deleteAll() {
        filterRepository.deleteAll();
    }

    public void save(Filter filter) {
        filterRepository.save(filter);
    }
}
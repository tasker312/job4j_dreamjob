package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Ivan", "i have no idea what is Java", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "John Ports", "best programmer", LocalDateTime.now(), 3, 0));
        save(new Candidate(0, "Adam Smith", "", LocalDateTime.now(), 2, 0));
        save(new Candidate(0, "Marcus Mitchel", "im professional", LocalDateTime.now(), 3, 0));
        save(new Candidate(0, "Victor Peterson", "", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "Harry Potter", "this is magic", LocalDateTime.now(), 1, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(
                candidate.getId(),
                (id, oldCandidate) -> candidate
        ) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}

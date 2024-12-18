package ru.job4j.dreamjob.repository;


import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Dasha", "Senior", LocalDateTime.now(), 0, 0));
        save(new Candidate(0, "Sasha", "Worked at Microsoft", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "Masha", "Was a teamlead at Yandex", LocalDateTime.now(), 0, 0));
        save(new Candidate(0, "Grisha", "Can do backflips", LocalDateTime.now(), 1, 0));
        save(new Candidate(0, "Misha", "Completed job4j courses", LocalDateTime.now(), 2, 0));
        save(new Candidate(0, "Yasha", "Need money", LocalDateTime.now(), 1, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> {
            return new Candidate(
                    oldCandidate.getId(), candidate.getName(),
                    candidate.getDescription(), oldCandidate.getCreationDate(),
                    candidate.getCityId(), candidate.getFileId());
        }) != null;
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
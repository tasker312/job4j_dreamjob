package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;

    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository candidateRepository, FileService fileService) {
        this.candidateRepository = candidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto file) {
        if (file.getContent().length != 0) {
            saveNewFile(candidate, file);
        }
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto file) {
        var savedFile = fileService.save(file);
        candidate.setFileId(savedFile.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var candidateFile = candidateRepository.findById(id);
        candidateFile.ifPresent(candidate -> fileService.deleteById(candidate.getFileId()));
        return candidateRepository.deleteById(id);
    }

    @Override
    public boolean update(Candidate candidate, FileDto file) {
        if (file.getContent().length != 0) {
            fileService.deleteById(candidate.getFileId());
            saveNewFile(candidate, file);
        }
        return candidateRepository.update(candidate);
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}

package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class SimpleVacancyService implements VacancyService {

    private final VacancyRepository vacancyRepository;

    private final FileService fileService;

    public SimpleVacancyService(VacancyRepository vacancyRepository, FileService fileService) {
        this.vacancyRepository = vacancyRepository;
        this.fileService = fileService;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto file) {
        if (file.getContent().length != 0) {
            saveNewFile(vacancy, file);
        }
        return vacancyRepository.save(vacancy);
    }

    private void saveNewFile(Vacancy vacancy, FileDto file) {
        var savedFile = fileService.save(file);
        vacancy.setFileId(savedFile.getId());
    }

    @Override
    public boolean deleteById(int id) {
        var vacancyFile = vacancyRepository.findById(id);
        vacancyFile.ifPresent(vacancy -> fileService.deleteById(vacancy.getFileId()));
        return vacancyRepository.deleteById(id);
    }

    @Override
    public boolean update(Vacancy vacancy, FileDto file) {
        if (file.getContent().length != 0) {
            fileService.deleteById(vacancy.getFileId());
            saveNewFile(vacancy, file);
        }
        return vacancyRepository.update(vacancy);
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}

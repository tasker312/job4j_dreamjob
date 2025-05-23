package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;

public interface VacancyService {

    Vacancy save(Vacancy vacancy, FileDto file);

    boolean deleteById(int id);

    boolean update(Vacancy vacancy, FileDto file);

    Optional<Vacancy> findById(int id);

    Collection<Vacancy> findAll();
}

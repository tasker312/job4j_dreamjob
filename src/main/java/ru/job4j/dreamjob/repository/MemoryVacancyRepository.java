package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    public MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "nothing to describe", true, 2));
        save(new Vacancy(0, "Junior Java Developer", "", false, 1));
        save(new Vacancy(0, "Junior+ Java Developer", "", true, 1));
        save(new Vacancy(0, "Middle Java Developer", "the best job in the best company", false, 3));
        save(new Vacancy(0, "Middle+ Java Developer", "", true, 3));
        save(new Vacancy(0, "Senior Java Developer", "", true, 2));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(
                vacancy.getId(),
                (id, oldVacancy) -> {
                    oldVacancy.setTitle(vacancy.getTitle());
                    oldVacancy.setDescription(vacancy.getDescription());
                    oldVacancy.setVisible(vacancy.getVisible());
                    oldVacancy.setCityId(vacancy.getCityId());
                    return oldVacancy;
                }
        ) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}

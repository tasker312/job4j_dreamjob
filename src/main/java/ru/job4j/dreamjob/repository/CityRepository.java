package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.City;

import java.util.Collection;

@Repository
public interface CityRepository {

    Collection<City> findAll();

}

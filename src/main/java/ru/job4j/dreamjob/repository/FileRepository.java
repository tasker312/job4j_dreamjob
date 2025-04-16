package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.File;

import java.util.Optional;

@Repository
public interface FileRepository {

    File save(File file);

    Optional<File> findById(int id);

    boolean deleteById(int id);

}

package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import java.time.temporal.ChronoUnit;
import java.util.Properties;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream =
                     Sql2oCandidateRepositoryTest.class.getClassLoader()
                             .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }

        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(dataSource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void deleteCandidates() {
        var candidates = sql2oCandidateRepository.findAll();
        candidates.forEach(candidate -> sql2oCandidateRepository.deleteById(candidate.getId()));
    }

    @Test
    public void whenEmptyRepositoryThenEmptyList() {
        var candidates = sql2oCandidateRepository.findAll();
        assertThat(candidates).isEmpty();
    }

    @Test
    public void whenSaveThenGetById() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = new Candidate(0, "Ivan", "programmer", creationDate, 3, file.getId());
        var savedCandidate = sql2oCandidateRepository.save(candidate);
        assertThat(savedCandidate).usingRecursiveAssertion().isEqualTo(candidate);
        assertThat(candidate).usingRecursiveComparison()
                .isEqualTo(sql2oCandidateRepository.findById(savedCandidate.getId()).get());
    }

    @Test
    public void whenFindByNonExistentIdThenOptionalEmpty() {
        var candidate = sql2oCandidateRepository.findById(1);
        assertThat(candidate).isEmpty();
    }

    @Test
    public void whenSaveManyThenEqualsSavedAndFindAll() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = new Candidate(0, "Ivan", "programmer", creationDate, 3, file.getId());
        var candidate2 = new Candidate(0, "Egor", "DJ", creationDate, 1, file.getId());
        var savedCandidate1 = sql2oCandidateRepository.save(candidate1);
        var savedCandidate2 = sql2oCandidateRepository.save(candidate2);
        var savedCandidates = sql2oCandidateRepository.findAll();

        assertThat(savedCandidate1).usingRecursiveAssertion().isEqualTo(candidate1);
        assertThat(savedCandidate2).usingRecursiveAssertion().isEqualTo(candidate2);
        assertThat(savedCandidates).containsExactlyInAnyOrder(savedCandidate1, savedCandidate2);
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(
                new Candidate(0, "Ivan", "programmer", creationDate, 3, file.getId()));
        var isDeleted = sql2oCandidateRepository.deleteById(candidate.getId());
        var isDeletedAgain = sql2oCandidateRepository.deleteById(candidate.getId());

        assertThat(isDeleted).isTrue();
        assertThat(isDeletedAgain).isFalse();
        assertThat(sql2oCandidateRepository.findById(candidate.getId())).isEmpty();
    }

    @Test
    public void whenDeleteNonExistentThenGetFalse() {
        var isDeleted = sql2oCandidateRepository.deleteById(1);
        assertThat(isDeleted).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        var creationDate = now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(
                new Candidate(0, "name", "description", creationDate, 1, file.getId()));
        var updatedCandidate = new Candidate(candidate.getId(), "new name", "new description",
                creationDate, 1, file.getId());
        var isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        var savedCandidate = sql2oCandidateRepository.findById(updatedCandidate.getId()).get();

        assertThat(isUpdated).isTrue();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    public void whenUpdateNonExistentThenGetFalse() {
        var isUpdated = sql2oCandidateRepository.update(new Candidate());
        assertThat(isUpdated).isFalse();
    }
}

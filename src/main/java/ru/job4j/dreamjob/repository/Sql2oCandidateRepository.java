package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oCandidateRepository implements CandidateRepository {

    private final Sql2o sql2o;

    public Sql2oCandidateRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Candidate save(Candidate candidate) {
        try (Connection connection = sql2o.open()) {
            var query = connection.createQuery(
                    """
                                INSERT INTO candidates (name, description, creation_date, city_id, file_id)
                                VALUES (:name, :description, :creationDate, :cityId, :fileId)
                            """,
                    true
            );
            query.addParameter("name", candidate.getName());
            query.addParameter("description", candidate.getDescription());
            query.addParameter("creationDate", candidate.getCreationDate());
            query.addParameter("cityId", candidate.getCityId());
            query.addParameter("fileId", candidate.getFileId() == 0 ? null : candidate.getFileId());
            candidate.setId(query.executeUpdate().getKey(Integer.class));
            return candidate;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM candidates WHERE id = :id");
            query.addParameter("id", id);
            return query.executeUpdate().getResult() != 0;
        }
    }

    @Override
    public boolean update(Candidate candidate) {
        try (Connection connection = sql2o.open()) {
            var query = connection.createQuery(
                    """
                                UPDATE candidates
                                SET name          = :name,
                                    description   = :description,
                                    city_id       = :cityId,
                                    file_id       = :fileId
                                WHERE id = :id
                            """);
            query.addParameter("name", candidate.getName());
            query.addParameter("description", candidate.getDescription());
            query.addParameter("cityId", candidate.getCityId());
            query.addParameter("fileId", candidate.getFileId() == 0 ? null : candidate.getFileId());
            query.addParameter("id", candidate.getId());
            var affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }

    @Override
    public Optional<Candidate> findById(int id) {
        try (Connection connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM candidates WHERE id = :id");
            query.addParameter("id", id);
            Candidate candidate = query.setColumnMappings(Candidate.COLUMN_MAPPING).executeAndFetchFirst(Candidate.class);
            return Optional.ofNullable(candidate);
        }
    }

    @Override
    public Collection<Candidate> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM candidates");
            return query.setColumnMappings(Candidate.COLUMN_MAPPING).executeAndFetch(Candidate.class);
        }
    }
}

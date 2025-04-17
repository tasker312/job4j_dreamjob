package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    protected static Sql2o sql2o;

    @BeforeAll
    public static void init() throws IOException {
        var properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }

        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var dataSource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(dataSource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void cleanUp() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    public void whenSaveUserThenReturnUser() {
        var user = new User(0, "john@doe.com", "John", "password");
        var savedUser = sql2oUserRepository.save(user);
        user.setId(savedUser.get().getId());

        assertThat(savedUser).get().usingRecursiveAssertion().isEqualTo(user);
    }

    @Test
    public void whenSaveManyUsersWithDifferentEmailsThenReturnUsers() {
        var user1 = sql2oUserRepository.save(
                new User(0, "john@doe.com", "John", "password"));
        var user2 = sql2oUserRepository.save(
                new User(0, "jane@dia.com", "Jane", "password"));

        assertThat(user1).isNotEmpty();
        assertThat(user2).isNotEmpty();
    }

    @Test
    public void whenSaveManyUsersWithSameEmailsThenException() {
        var user = new User(0, "john@doe.com", "John", "password");
        sql2oUserRepository.save(user);
        assertThatThrownBy(() -> sql2oUserRepository.save(user))
                .isInstanceOf(Sql2oException.class)
                .hasMessageContaining("Unique index or primary key violation");
    }

    @Test
    public void whenFindByEmailAndPasswordThenReturnUser() {
        var user = sql2oUserRepository.save(
                new User(0, "john@doe.com", "John", "password"));
        var foundUser = sql2oUserRepository.findByEmailAndPassword(
                user.get().getEmail(), user.get().getPassword());
        assertThat(foundUser).isNotEmpty().get().usingRecursiveAssertion().isEqualTo(user.get());
    }

    @Test
    public void whenFindByNonExistentEmailThenReturnOptionalEmpty() {
        assertThat(sql2oUserRepository.findByEmailAndPassword("", null)).isEmpty();
    }

    @Test
    public void whenSaveNullFieldThenException() {
        assertThatThrownBy(() -> sql2oUserRepository.save(new User(0, null, null, null)))
                .isInstanceOf(Sql2oException.class)
                .hasMessageContaining("NULL not allowed");
    }

}

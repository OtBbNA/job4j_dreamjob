package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Properties;

import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepository.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");
        var configuration = new DatasourceConfiguration();

        var datasource = configuration.connectionPool(url, username, password);

        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteByEmail(user.getEmail());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        String email = "findById@mail.ru";
        String password = "4364363";
        var user = sql2oUserRepository.save(new User(0, email, "SuNashedshiy", password));
        var savedUser = sql2oUserRepository.findByEmailAndPassword(email, password);
        assertThat(savedUser.get()).usingRecursiveComparison().isEqualTo(user.get());
    }

    @Test
    void whenSaveSameEmailThenGetEmptyOptional() {
        String email = "nagibator2015@mail.ru";
        String password = "4364363";
        var user1 = sql2oUserRepository.save(new User(0, email, "Nagibator123", password));
        var user2 = sql2oUserRepository.save(new User(0, email, "borov", password));
        assertThat(user2).isEqualTo(empty());
    }

}
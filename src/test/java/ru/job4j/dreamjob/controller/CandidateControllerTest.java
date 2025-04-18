package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void setUp() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestAllThenCandidatesPage() {
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Edinburgh");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        var candidate1 = new Candidate(1, "Ivan", "Programmer", now(), 1, 1);
        var candidate2 = new Candidate(2, "Egor", "Programmer", now(), 2, 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
        assertThat(actualCities).isEqualTo(expectedCities);

    }

    @Test
    public void whenRequestCreationPageThenCreationPageWithCities() {
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Edinburgh");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCities).isEqualTo(expectedCities);

    }

    @Test
    public void whenPostCandidateThenRedirectToCandidatesPage() throws IOException {
        var candidate = new Candidate(1, "Ivan", "Programmer", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var fileCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(any(), fileCaptor.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(fileCaptor.getValue()).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenPostCreateSomeExceptionThrownThenErrorPage() {
        var exception = new RuntimeException("Test Exception");
        when(candidateService.save(any(), any())).thenThrow(exception);

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualMessage).isEqualTo(exception.getMessage());
    }

    @Test
    public void whenRequestEditThenEditPageWithCandidateAndCities() {
        var city1 = new City(1, "Moscow");
        var city2 = new City(2, "Edinburgh");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        var candidate1 = new Candidate(1, "Ivan", "Programmer", now(), 1, 1);
        when(candidateService.findById(candidate1.getId())).thenReturn(Optional.of(candidate1));

        var model = new ConcurrentModel();
        var view = candidateController.getById(candidate1.getId(), model);
        var actualCandidate = model.getAttribute("candidate");
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/edit");
        assertThat(actualCandidate).usingRecursiveComparison().isEqualTo(candidate1);
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    public void whenRequestEditWithNonExistentCandidateThenErrorPage() {
        when(candidateService.findById(1)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = candidateController.getById(1, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat((String) actualMessage)
                .isNotNull()
                .contains("Candidate")
                .contains("not found");
    }

    @Test
    public void whenUpdateCandidateThenRedirectToCandidatesPage() {
        when(candidateService.update(any(), any())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenUpdateNonExistentCandidateThenErrorPage() {
        when(candidateService.update(any(), any())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat((String) actualMessage)
                .isNotNull()
                .contains("Candidate")
                .contains("not found");
    }

    @Test
    public void whenDeleteCandidateThenRedirectToCandidatesPage() {
        when(candidateService.deleteById(1)).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(1, model);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenDeleteNonExistentCandidateThenErrorPage() {
        when(candidateService.deleteById(1)).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(1, model);
        var actualMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat((String) actualMessage)
                .isNotNull()
                .contains("Candidate")
                .contains("not found");
    }
}

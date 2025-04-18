package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    private FileDto fileDto;

    @BeforeEach
    public void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        fileDto = mock(FileDto.class);
    }

    @Test
    public void whenRequestFileThenResponseOkWithFile() {
        var expectedResponse = HttpStatus.OK;
        when(fileService.getFileById(1)).thenReturn(Optional.of(fileDto));

        var actualResponse = fileController.getById(1);

        assertThat(actualResponse.getStatusCode()).isEqualTo(expectedResponse);
    }

    @Test
    public void whenRequestNonExistentFileThenResponseNotFound() {
        var expectedResponse = ResponseEntity.notFound().build();
        when(fileService.getFileById(1)).thenReturn(Optional.empty());

        var actualResponse = fileController.getById(1);

        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}

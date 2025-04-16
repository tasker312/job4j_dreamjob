package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.SimpleVacancyService;
import ru.job4j.dreamjob.service.VacancyService;

@Controller
@RequestMapping("/vacancies") /* Работать с кандидатами будем по URI /vacancies/** */
public class VacancyController {

    private final VacancyService vacancyService = SimpleVacancyService.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy) {
        vacancyService.save(vacancy);
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        var vacancy = vacancyService.findById(id);
        if (vacancy.isEmpty()) {
            model.addAttribute("message", "Vacancy with id %d not found.".formatted(id));
            return "errors/404";
        }
        model.addAttribute("vacancy", vacancy.get());
        return "vacancies/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Vacancy vacancy, Model model) {
        boolean isUpdated = vacancyService.update(vacancy);
        if (!isUpdated) {
            model.addAttribute("message", "Vacancy with id %d not found.".formatted(vacancy.getId()));
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        boolean isDeleted = vacancyService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Vacancy with id %d not found.".formatted(id));
            return "errors/404";
        }
        return "redirect:/vacancies";
    }
}

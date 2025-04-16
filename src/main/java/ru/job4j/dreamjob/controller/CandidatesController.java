package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

@Controller
@RequestMapping("/candidates")
public class CandidatesController {

    private final CandidateService candidateService;
    private final CityService cityService;

    public CandidatesController(CandidateService candidateService, CityService cityService) {
        this.candidateService = candidateService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateService.findAll());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate) {
        candidateService.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        var candidate = candidateService.findById(id);
        if (candidate.isEmpty()) {
            model.addAttribute("message", "Candidate with id %d not found.".formatted(id));
            return "errors/404";
        }
        model.addAttribute("candidate", candidate.get());
        model.addAttribute("cities", cityService.findAll());
        return "candidates/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        boolean isUpdated = candidateService.update(candidate);
        if (!isUpdated) {
            model.addAttribute("message", "Candidate with id %d not found.".formatted(candidate.getId()));
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        boolean isDeleted = candidateService.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Candidate with id %d not found.".formatted(id));
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}

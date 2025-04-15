package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;

@Controller
@RequestMapping("/candidates")
public class CandidatesController {

    private final CandidateRepository candidateRepository = MemoryCandidateRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", candidateRepository.findAll());
        return "candidates/list";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate) {
        candidateRepository.save(candidate);
        return "redirect:/candidates";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable int id, Model model) {
        var candidate = candidateRepository.findById(id);
        if (candidate.isEmpty()) {
            model.addAttribute("message", "Candidate with id %d not found.".formatted(id));
            return "errors/404";
        }
        model.addAttribute("candidate", candidate.get());
        return "candidates/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Candidate candidate, Model model) {
        boolean isUpdated = candidateRepository.update(candidate);
        if (!isUpdated) {
            model.addAttribute("message", "Candidate with id %d not found.".formatted(candidate.getId()));
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, Model model) {
        boolean isDeleted = candidateRepository.deleteById(id);
        if (!isDeleted) {
            model.addAttribute("message", "Candidate with id %d not found.".formatted(id));
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}

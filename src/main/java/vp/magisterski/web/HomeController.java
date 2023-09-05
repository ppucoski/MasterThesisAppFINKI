package vp.magisterski.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vp.magisterski.service.MasterThesisService;

@Controller
@RequestMapping({"/",  "/index"})
public class HomeController {
    private final MasterThesisService masterThesisService;

    public HomeController(MasterThesisService masterThesisService) {
        this.masterThesisService = masterThesisService;
    }

    @GetMapping
    public String getHomePage(){
        return "index";
    }


    @GetMapping("/MasterThesis-procedure")
    public String getThesisProcedurePage() {
        return "index/MasterThesis-procedure";
    }


    @GetMapping("/list-MasterThesis")
    public String findAllThesis(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        model.addAttribute("MasterThesis", masterThesisService.findAll(pageable));
        return "index/list-MasterThesis";
    }

}

package vp.magisterski.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vp.magisterski.config.FacultyUserDetails;
import vp.magisterski.model.shared.User;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping({"/",  "/index"})
public class HomeController {
    private final MasterThesisService masterThesisService;
    private final UserService userService;

    public HomeController(MasterThesisService masterThesisService, UserService userService) {
        this.masterThesisService = masterThesisService;
        this.userService = userService;
    }

    @ModelAttribute
    public void trackUsername(Model model){
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }


    @GetMapping
    public String getHomePage(Model model){
        return "index";
    }

    @GetMapping("/list-MasterThesis")
    public String findAllThesis(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        int total = masterThesisService.findAll().size();
        model.addAttribute("MasterThesis", masterThesisService.findAll(pageable));
        model.addAttribute("total", total);
        return "MasterThesisList";
    }

}
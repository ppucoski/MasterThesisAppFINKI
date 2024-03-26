package vp.magisterski.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.UserService;

@Controller
@RequestMapping({"/", "/index"})
public class HomeController {
    private final MasterThesisService masterThesisService;
    private final UserService userService;

    public HomeController(MasterThesisService masterThesisService, UserService userService) {
        this.masterThesisService = masterThesisService;
        this.userService = userService;
    }

    @ModelAttribute
    public void trackUsername(Model model) {
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }


    @GetMapping
    public String getHomePage() {
        return "index";
    }

    @GetMapping("/list-MasterThesis")
    public String findAllThesis(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        model.addAttribute("MasterThesis", masterThesisService.findAllByStatus(MasterThesisStatus.PROCESS_FINISHED, pageable));
        return "MasterThesisList";
    }

}

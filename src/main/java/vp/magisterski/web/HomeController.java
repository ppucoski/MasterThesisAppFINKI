package vp.magisterski.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.service.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping({"/", "/index"})
public class HomeController {
    @Autowired
    private HttpServletRequest request;

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final MasterThesisService masterThesisService;
    private final UserService userService;


    public HomeController(StudentService studentService, ProfessorService professorService, MasterThesisService masterThesisService, UserService userService) {
        this.studentService = studentService;
        this.professorService = professorService;
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
    public String findAllThesis(@RequestParam(required = false) String index,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false) MasterThesisStatus status,
                                @RequestParam(required = false) String mentor,
                                @RequestParam(required = false) String firstMember,
                                @RequestParam(required = false) String secondMember,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "3") int size,
                                @RequestParam(required = false) String isValidation,
                                Model model) {
        String currentUrl = request.getRequestURI() + "?" + request.getQueryString();
        currentUrl = currentUrl.replaceFirst("(\\?|&)?page=[^&]*", "");
        currentUrl = currentUrl.replaceFirst("(\\?|&)?size=[^&]*", "");
        model.addAttribute("currentUrl", currentUrl);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Professor mentor1 = this.professorService.findProfessorById(mentor).orElse(null);
        Professor firstMember1 = this.professorService.findProfessorById(firstMember).orElse(null);
        Professor secondMember1 = this.professorService.findProfessorById(secondMember).orElse(null);

        Specification<MasterThesis> specification = Specification.where(null);

        if (firstMember1 != null && secondMember1 != null) {
            specification = specification.and(this.masterThesisService.filterMasterThesisByMember(firstMember1, secondMember1));
        } else {
            if (firstMember1 != null) {
                specification = specification.and(this.masterThesisService.filterMasterThesisByFirstMember(firstMember1));
            }

            if (secondMember1 != null) {
                specification = specification.and(this.masterThesisService.filterMasterThesisBySecondMember(secondMember1));
            }
        }
        specification = specification.and(this.masterThesisService.filterMasterThesisByStatus(status));

        specification = specification.and(this.masterThesisService.filterMasterThesis(index, title, status, mentor1, firstMember1, secondMember1, isValidation));

        Page<MasterThesis> masterThesis = masterThesisService.findAll(specification, pageable);


        model.addAttribute("master_page", masterThesis);
        model.addAttribute("size", masterThesis.stream().toList().size());
        model.addAttribute("master_members", professorService.findAll());
        model.addAttribute("master_status", masterThesisService.returnStatus());
        model.addAttribute("master_mentors", this.professorService.findAll());

        model.addAttribute("currentIndex", index);
        model.addAttribute("currentTitle", title);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentMentor", mentor1);
        model.addAttribute("currentFirstMember", firstMember1);
        model.addAttribute("currentSecondMember", secondMember1);
        model.addAttribute("MasterThesis", masterThesis);
        return "MasterThesisList";
    }

    @GetMapping("/resetFilterHome")
    public String resetFilter() {
        return "redirect:list-MasterThesis";
    }
}

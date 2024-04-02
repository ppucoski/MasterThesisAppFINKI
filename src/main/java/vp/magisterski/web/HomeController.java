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

    public List<MasterThesisStatus> masterThesisToShow(){
        List<MasterThesisStatus> statusList = new ArrayList<>();
        statusList.add(MasterThesisStatus.MENTOR_COMMISSION_CHOICE);
        statusList.add(MasterThesisStatus.SECOND_SECRETARY_VALIDATION);
        statusList.add(MasterThesisStatus.COMMISSION_CHECK);
        statusList.add(MasterThesisStatus.THIRD_SECRETARY_VALIDATION);
        statusList.add(MasterThesisStatus.DRAFT_CHECK);
        statusList.add(MasterThesisStatus.REPORT_VALIDATION);
        statusList.add(MasterThesisStatus.FOURTH_SECRETARY_VALIDATION);
        statusList.add(MasterThesisStatus.ADMINISTRATION_ARCHIVING);
        statusList.add(MasterThesisStatus.PROCESS_FINISHED);
        return statusList;

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
        Student student = this.studentService.findStudentById(index).orElse(null);
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

        specification = specification.and(this.masterThesisService.filterMasterThesis(student, title, status, mentor1, firstMember1, secondMember1, isValidation));

        Page<MasterThesis> master_page = this.masterThesisService.findAll(specification, pageable);

        model.addAttribute("master_page", master_page);
        model.addAttribute("size", master_page.getTotalElements());
        model.addAttribute("master_status", MasterThesisStatus.values());
        model.addAttribute("master_mentors", this.professorService.findAll());
        model.addAttribute("master_members", professorService.findAll());

        model.addAttribute("currentIndex", index);
        model.addAttribute("currentTitle", title);
        model.addAttribute("currentStatus", status);
        model.addAttribute("currentMentor", mentor1);
        model.addAttribute("currentFirstMember", firstMember1);
        model.addAttribute("currentSecondMember", secondMember1);
        model.addAttribute("MasterThesis", masterThesisService.findAllByStatusOrderGreaterThan(masterThesisToShow(), pageable));
        model.addAttribute("reset", "/resetFilterHome");
        model.addAttribute("back", "/list-MasterThesis");
        return "MasterThesisList";
    }

    @GetMapping("/goBack")
    public String goBack(Model model) {
        return "MasterThesisList";
    }

    @GetMapping("/resetFilterHome")
    public String resetFilter() {
        return "redirect:list-MasterThesis";
    }
}

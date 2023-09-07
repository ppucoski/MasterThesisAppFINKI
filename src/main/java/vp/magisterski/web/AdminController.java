package vp.magisterski.web;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.ProfessorService;
import vp.magisterski.service.StudentService;

import java.util.Collections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final MasterThesisService masterThesisService;

    public AdminController(StudentService studentService, ProfessorService professorService, MasterThesisService masterThesisService) {
        this.studentService = studentService;
        this.professorService = professorService;
        this.masterThesisService = masterThesisService;
    }

    @GetMapping("/list-masters")
    public String showMasterList(@RequestParam(required = false) String index,
                                 @RequestParam(required = false) String title,
                                 @RequestParam(required = false) MasterThesisStatus status,
                                 @RequestParam(required = false) String mentor,
                                 @RequestParam(required = false) String member,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size, Model model)
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Student student = this.studentService.findStudentById(index).orElse(null);
        Professor mentor1 = this.professorService.findProfessorById(mentor).orElse(null);
        Professor member1 = this.professorService.findProfessorById(member).orElse(null);
        MasterThesis masterThesis = new MasterThesis(status, student, title, mentor1, member1);
        Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesis(masterThesis);

        Page<MasterThesis> master_page = this.masterThesisService.findAll(specification, pageable);



        model.addAttribute("master_page", master_page);
        model.addAttribute("master_status", MasterThesisStatus.values());
        model.addAttribute("master_mentors", this.professorService.findAll());
        model.addAttribute("master_members", professorService.findAll());


        model.addAttribute("selectedMentor", mentor != null ? mentor : "");
        model.addAttribute("selectedStatus", status != null ? status : "");
        model.addAttribute("selectedMember", member != null ? member : "");


        return "list_masters";
    }
    @PostMapping("/list-masters")
    public String filterMasterList(@RequestParam(required = false) String index,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) MasterThesisStatus status,
            @RequestParam(required = false) String mentor,
            @RequestParam(required = false) String member,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size, Model model)
    {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));

        Student student = this.studentService.findStudentById(index).orElse(null);
        Professor mentor1 = this.professorService.findProfessorById(mentor).orElse(null);
        Professor member1 = this.professorService.findProfessorById(member).orElse(null);

        if((student == null && index.isEmpty()) || (student != null && !index.isEmpty()))
        {
            MasterThesis masterThesis = new MasterThesis(status, student, title, mentor1, member1);
            Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesis(masterThesis);

            Page<MasterThesis> master_page = this.masterThesisService.findAll(specification, pageable);

            model.addAttribute("master_page", master_page);
            model.addAttribute("master_page_total_elements", master_page.getTotalElements());
        }
        else
        {
            MasterThesis empty = new MasterThesis();
            Specification<MasterThesis> emptySpec = this.masterThesisService.filterMasterThesis(empty);
            Page<MasterThesis> emptyPage = this.masterThesisService.findAll(emptySpec, pageable);
            model.addAttribute("master_page", emptyPage);
            model.addAttribute("master_page_total_elements", 0);
        }
        model.addAttribute("master_status", MasterThesisStatus.values());
        model.addAttribute("master_mentors", this.professorService.findAll());
        model.addAttribute("master_members", professorService.findAll());


        model.addAttribute("selectedMentor", mentor != null ? mentor : "");
        model.addAttribute("selectedStatus", status != null ? status : "");
        model.addAttribute("selectedMember", member != null ? member : "");


        return "list_masters";
    }

    @GetMapping("/newMasterThesis")
    public String newMasterThesis(Model model) {
        model.addAttribute("professors", professorService.findAllByProfessorStatus(true, false));
        model.addAttribute("members", professorService.findAllByProfessorStatus(true, true));
        return "newMasterThesis";
    }

    @PostMapping("/newMasterThesis")
    public String saveNewMasterThesis(@RequestParam String index,
                                      @RequestParam String title,
                                      @RequestParam String area,
                                      @RequestParam String description,
                                      @RequestParam String mentor,
                                      @RequestParam String firstMember,
                                      @RequestParam String secondMember) {
        try {
            masterThesisService.save(index, LocalDateTime.now(), title, area, description, mentor, firstMember, secondMember);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "index";
    }
}

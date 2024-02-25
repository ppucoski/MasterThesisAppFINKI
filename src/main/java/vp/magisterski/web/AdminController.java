package vp.magisterski.web;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.config.FacultyUserDetails;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisStatus;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.model.shared.User;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.ProfessorService;
import vp.magisterski.service.StudentService;
import vp.magisterski.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final MasterThesisService masterThesisService;
    private final UserService userService;

    public AdminController(StudentService studentService, ProfessorService professorService, MasterThesisService masterThesisService, UserService userService) {
        this.studentService = studentService;
        this.professorService = professorService;
        this.masterThesisService = masterThesisService;
        this.userService = userService;
    }

    @ModelAttribute
    public void trackUsername(Model model){
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }

    @GetMapping("/list-masters")
    public String showMasterList(@RequestParam(required = false) String index,
                                 @RequestParam(required = false) String title,
                                 @RequestParam(required = false) MasterThesisStatus status,
                                 @RequestParam(required = false) String mentor,
                                 @RequestParam(required = false) String member,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "2") int size,
                                 @RequestParam(required = false) String isValidation,
                                 Model model)
    {
        Pageable pageable = PageRequest.of(page, size);
        Student student = this.studentService.findStudentById(index).orElse(null);
        Professor mentor1 = this.professorService.findProfessorById(mentor).orElse(null);
        Professor member1 = this.professorService.findProfessorById(member).orElse(null);
        Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesis(student, title, status, mentor1, member1, isValidation);

        Page<MasterThesis> master_page = this.masterThesisService.findAll(specification, pageable);

        model.addAttribute("master_page", master_page);
        model.addAttribute("master_status", MasterThesisStatus.values());
        model.addAttribute("master_mentors", this.professorService.findAll());
        model.addAttribute("master_members", professorService.findAll());

        model.addAttribute("selectedMentor", mentor != null ? mentor : "");
        model.addAttribute("selectedStatus", status != null ? status : "");
        model.addAttribute("selectedMember", member != null ? member : "");

        model.addAttribute("isValidation", isValidation != null ? isValidation : "");

        return "list_masters";
    }


    @PostMapping("/list-masters")
    public String filterMasterList(@RequestParam(required = false) String index,
                                   @RequestParam(required = false) String title,
                                   @RequestParam(required = false) MasterThesisStatus status,
                                   @RequestParam(required = false) String mentor,
                                   @RequestParam(required = false) String member,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String isValidation,
                                   Model model) {
        Pageable pageable = PageRequest.of(page, size);

        Student student = this.studentService.findStudentById(index).orElse(null);
        Professor mentor1 = this.professorService.findProfessorById(mentor).orElse(null);
        Professor member1 = this.professorService.findProfessorById(member).orElse(null);

        if((student == null && index.isEmpty()) || (student != null && !index.isEmpty())) {

            Specification<MasterThesis> specification = masterThesisService.filterMasterThesis(student, title, status, mentor1, member1, isValidation);

            Page<MasterThesis> masterFilteredPage = this.masterThesisService.findAll(specification, pageable);


            model.addAttribute("master_page", masterFilteredPage);
            model.addAttribute("master_page_total_elements", masterFilteredPage.getTotalElements());
        }
        else
        {
            MasterThesis empty = new MasterThesis();
            Specification<MasterThesis> emptySpec = this.masterThesisService.filterMasterThesis(empty, "");
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

        return "redirect:list-masters";
    }

    @GetMapping("/upload")
    public String uploadThesisFile(Model model, @RequestParam Long thesisId) {
        model.addAttribute("thesisId", thesisId);
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadThesisFile(@RequestParam Long thesisId, @RequestParam("file") MultipartFile file) throws IOException {
        masterThesisService.saveFile(thesisId, file);
        return "redirect:list-masters";
    }

    @GetMapping("/download/{thesisId}")
    public ResponseEntity<ByteArrayResource> downloadThesis(@PathVariable Long thesisId) {
        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "MasterThesis.pdf");
        ByteArrayResource resource = new ByteArrayResource(masterThesis.getThesisText());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(masterThesis.getThesisText().length)
                .body(resource);
    }
}
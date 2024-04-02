package vp.magisterski.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import vp.magisterski.model.enumerations.AppRole;
import vp.magisterski.model.enumerations.MasterThesisDocumentType;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.model.shared.UserRole;
import vp.magisterski.service.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final MasterThesisService masterThesisService;
    private final UserService userService;
    private final MasterThesisStatusChangeService masterThesisStatusChangeService;
    private final MasterThesisDocumentService masterThesisDocumentService;
    private final RoomService roomService;

    @Autowired
    private HttpServletRequest request;

    public AdminController(StudentService studentService, ProfessorService professorService, MasterThesisService masterThesisService, UserService userService, MasterThesisStatusChangeService masterThesisStatusChangeService, MasterThesisDocumentService masterThesisDocumentService, RoomService roomService) {
        this.studentService = studentService;
        this.professorService = professorService;
        this.masterThesisService = masterThesisService;
        this.userService = userService;
        this.masterThesisStatusChangeService = masterThesisStatusChangeService;
        this.masterThesisDocumentService = masterThesisDocumentService;
        this.roomService = roomService;
    }

    @ModelAttribute
    public void trackUsername(Model model) {
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }



    @GetMapping("/list-masters")
    public String showMasterList(
            @RequestParam(required = false) String index,
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

        Pageable pageable = PageRequest.of(page, size);
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
        model.addAttribute("isValidation", isValidation);
        model.addAttribute("reset", "/admin/resetFilter");
        model.addAttribute("back", "/admin/list-masters");
        return "list_masters";
    }

    @GetMapping("/goBack")
    public String goBack(Model model) {
        return "list_masters";
    }


    @GetMapping("/resetFilter")
    public String resetFilter() {
        return "redirect:list-masters";
    }


    @GetMapping("/newMasterThesis")
    public String newMasterThesis(Model model) {
        model.addAttribute("professors", professorService.findAllByProfessorStatus(true, false));
        model.addAttribute("members", professorService.findAllByProfessorStatus(true, true));
        return "newMasterThesis";
    }

    @GetMapping("/masterThesisInfo")
    public String getMasterThesisInfo(Model model) {
        String username = userService.getUsernameFromUser();
        Professor thesisMentor = professorService.findProfessorByName(username);
        model.addAttribute("thesisMentor", thesisMentor);
        return "masterThesisInfo";
    }

    @PostMapping("/masterThesisInfo")
    public String filterThesis(@RequestParam String filter) {
        if (filter.equals("mentor")) {
            return "redirect:masterThesisMentorInfo";
        }
        return "redirect:masterThesisMemberInfo";
    }

    @GetMapping("/masterThesisMentorInfo")
    public String getMasterThesisMentorInfo(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "3") int size,
                                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        String username = userService.getUsernameFromUser();
        Professor mentor = professorService.findProfessorByName(username);
        Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesisByMentor(mentor);
        Page<MasterThesis> thesisPage = this.masterThesisService.findAll(specification, pageable);
        model.addAttribute("master_page", thesisPage);
        model.addAttribute("size", thesisPage.getTotalElements());
        return "masterThesisMentorInfo";
    }

    @GetMapping("/masterThesisMemberInfo")
    public String getMasterThesisMemberInfo(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "3") int size,
                                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        String username = userService.getUsernameFromUser();
        Professor mentor = professorService.findProfessorByName(username);
        Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesisByMember(mentor, mentor); //TODO: proveri dal e mentor ili member filter
        Page<MasterThesis> thesisPage = this.masterThesisService.findAll(specification, pageable);
        model.addAttribute("master_page", thesisPage);
        model.addAttribute("size", thesisPage.getTotalElements());
        return "masterThesisMemberInfo";
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

    @GetMapping("/details/{thesisId}")
    public String details(Model model, @PathVariable Long thesisId) {
        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
        List<MasterThesisDocument> associatedDocuments = masterThesisDocumentService.findAllByThesis(masterThesis);
        Optional<MasterThesisStatusChange> masterThesisStatusChange = masterThesisStatusChangeService.getStatusChange(masterThesis);
        model.addAttribute("thesis", masterThesis);
        model.addAttribute("masterThesisStatusChange", masterThesisStatusChange);
        model.addAttribute("associatedDocuments", associatedDocuments);
        List<MasterThesisStatusChange> temp = masterThesisStatusChangeService.getAllByThesis(masterThesis);
        model.addAttribute("allChanges", temp);

        model.addAttribute("role", userService.getUser().getRole());
        model.addAttribute("mentor", Objects.equals(masterThesis.getMentor().getId(), userService.getUser().getId()));
        model.addAttribute("student", false);


        if (masterThesis.getStatus().getNextStatusFromCurrent() == MasterThesisStatus.MENTOR_COMMISSION_CHOICE) {
            model.addAttribute("members", professorService.findAllByProfessorStatus(true, true));
        }
        if (masterThesis.getStatus().getNextStatusFromCurrent() == MasterThesisStatus.PROCESS_FINISHED) {
            model.addAttribute("rooms", roomService.findAll());
        }

        return "masterThesisDetails";
    }

    @PostMapping("/details/{statusId}")
    public String updateDetails(@RequestParam String note, @PathVariable Long statusId,
                                @RequestParam Long thesisId, @RequestParam(required = false) MultipartFile fileInput1,
                                @RequestParam(required = false) String action) throws IOException {
        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();

        if (action == null) {
            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
        }

        if (action != null && action.equals("reject")) {
            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), false);
        }
        if (action != null && action.equals("approve")) {
            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
        }

        if (fileInput1 != null) {
            masterThesisDocumentService.saveFile(masterThesis, MasterThesisDocumentType.THESIS_TEXT, fileInput1);
        }

        return String.format("redirect:/admin/details/%d", thesisId);
    }

    @PostMapping("/commissionUpdate/{statusId}")
    public String commissionUpdate(@PathVariable Long statusId,
                                   @RequestParam String firstMember,
                                   @RequestParam String secondMember,
                                   @RequestParam Long thesisId,
                                   @RequestParam String note) {
        try {
            MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
            masterThesisService.setCommission(thesisId, firstMember, secondMember);
            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return String.format("redirect:/admin/details/%d", thesisId);
    }

    @PostMapping("/presentationDateAndLocation/{statusId}")
    public String presentationDateAndLocation(@PathVariable Long statusId,
                                              @RequestParam String room,
                                              @RequestParam Long thesisId,
                                              @RequestParam String note, @RequestParam LocalDateTime localDateTime) {
        try {
            MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
            masterThesisService.updateLocationAndDate(thesisId, room, localDateTime);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return String.format("redirect:/admin/details/%d", thesisId);
    }


    @PostMapping("/firstStatusFragmentOblast/{statusId}")
    public String oblastUpdate(@PathVariable Long statusId,
                               @RequestParam Long thesisId,
                               @RequestParam String area,
                               @RequestParam String note) {
        try {
            MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
            masterThesisService.updateOblast(thesisId, area);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return String.format("redirect:/admin/details/%d", thesisId);
    }

    @PostMapping("/archiveNumber/{statusId}")
    public String archiveNumber(@PathVariable Long statusId,
                                @RequestParam String archiveNumber,
                                @RequestParam Long thesisId,
                                @RequestParam(required = false) String note) {
        try {
            MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();

            masterThesisStatusChangeService.updateStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
            masterThesisService.updateArchiveNumber(thesisId, archiveNumber);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return String.format("redirect:/admin/details/%d", thesisId);
    }

}

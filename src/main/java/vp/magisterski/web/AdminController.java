package vp.magisterski.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import vp.magisterski.model.exceptions.ThesisDoesNotExistException;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.magister.MasterThesisDocument;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.model.magister.MasterThesisStatusChange;
import vp.magisterski.model.shared.Professor;
import vp.magisterski.model.shared.Student;
import vp.magisterski.model.shared.User;
import vp.magisterski.model.shared.UserRole;
import vp.magisterski.service.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        User fullUser = userService.getUser();
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
        model.addAttribute("fullUser", fullUser);
    }

    private String processMasterThesisInfoForFiltering(
            String index, String title, MasterThesisStatus status,
            String mentor, String firstMember, String secondMember,
            String isValidation, int page, int size, Model model, String viewName) {

        String currentUrl = request.getRequestURI() + "?" + request.getQueryString();
        currentUrl = currentUrl.replaceFirst("(\\?|&)?page=[^&]*", "");
        currentUrl = currentUrl.replaceFirst("(\\?|&)?size=[^&]*", "");
        model.addAttribute("currentUrl", currentUrl);

        Pageable pageable = PageRequest.of(page, size);

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

        specification = specification.and(this.masterThesisService.filterMasterThesis(index, title, status, mentor1, firstMember1, secondMember1, isValidation));

        //TODO da proverime dali ova rabotat sea site se bez vreme pa ne znajme dali rabotat
        specification = specification.and((root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.desc(root.get("lastUpdate")));
            return null;
        });
        //TODO do ovde

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

        return viewName;
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

        return processMasterThesisInfoForFiltering(index, title, status, mentor, firstMember, secondMember, isValidation, page, size, model, "list_masters");
    }

    @GetMapping("/resetFilter")
    public String resetFilter() {
        return "redirect:list-masters";
    }

    @GetMapping("/resetFilterMentorInfo")
    public String resetFilterMentorInfo() {
        return "redirect:masterThesisMentorInfo";
    }

    @GetMapping("/resetFilterMemberInfo")
    public String resetFilterMemberInfo() {
        return "redirect:masterThesisMemberInfo";
    }




    @GetMapping("/masterThesisMentorInfo")
    public String getMasterThesisMentorInfo(
                                            @RequestParam(required = false) String index,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) MasterThesisStatus status,
                                            @RequestParam(required = false) String mentor,
                                            @RequestParam(required = false) String firstMember,
                                            @RequestParam(required = false) String secondMember,
                                            @RequestParam(required = false) String isValidation,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "3") int size,
                                            Model model) {
//        String username = userService.getUsernameFromUser();
//        Professor mentor = professorService.findProfessorByName(username);
        return processMasterThesisInfoForFiltering(index, title, status, mentor, firstMember, secondMember, isValidation, page, size, model, "masterThesisMentorInfo");
//        String username = userService.getUsernameFromUser();
//        Professor mentor = professorService.findProfessorByName(username);
//        Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesisByMentor(mentor);
//        Page<MasterThesis> thesisPage = this.masterThesisService.findAll(specification, pageable);
//        model.addAttribute("master_page", thesisPage);
//        model.addAttribute("size", thesisPage.getTotalElements());
    }

    @GetMapping("/masterThesisMemberInfo")
    public String getMasterThesisMemberInfo(
                                            @RequestParam(required = false) String index,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) MasterThesisStatus status,
                                            @RequestParam(required = false) String mentor,
                                            @RequestParam(required = false) String firstMember,
                                            @RequestParam(required = false) String secondMember,
                                            @RequestParam(required = false) String isValidation,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "3") int size,
                                            Model model) {
        return processMasterThesisInfoForFiltering(index, title, status, mentor, firstMember, secondMember, isValidation, page, size, model, "masterThesisMemberInfo");
//        Pageable pageable = PageRequest.of(page, size);
//        String username = userService.getUsernameFromUser();
//        Professor mentor = professorService.findProfessorByName(username);
//        Specification<MasterThesis> specification = this.masterThesisService.filterMasterThesisByMember(mentor, mentor); //TODO: proveri dal e mentor ili member filter
//        Page<MasterThesis> thesisPage = this.masterThesisService.findAll(specification, pageable);
//        model.addAttribute("master_page", thesisPage);
//        model.addAttribute("size", thesisPage.getTotalElements());
    }



    @GetMapping("/upload")
    public String uploadThesisFile(Model model, @RequestParam Long thesisId) {
        model.addAttribute("thesisId", thesisId);
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadThesisFile(@RequestParam Long thesisId, @RequestParam("file") MultipartFile file) throws IOException {
        masterThesisService.saveFile(thesisId, MasterThesisDocumentType.THESIS_TEXT, file);
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
        //model.addAttribute("coordinator", Objects.equals(masterThesis.getCoordinator().getId(), userService.getUser().getId()));
        model.addAttribute("grade", masterThesis.getGrade() != null);
        if (masterThesis.getFirstMember() == null || masterThesis.getSecondMember() == null) {
            model.addAttribute("member", false);
        } else {
            model.addAttribute("member", Objects.equals(masterThesis.getFirstMember().getId(), userService.getUser().getId()) ||
                    Objects.equals(masterThesis.getSecondMember().getId(), userService.getUser().getId()));
        }

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
            masterThesisService.saveFile(thesisId, MasterThesisDocumentType.THESIS_TEXT, fileInput1);
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


    @GetMapping("/edit/{thesisId}")
    public String editMasterThesis(@PathVariable Long thesisId, Model model) {
        MasterThesis thesis = masterThesisService.findThesisById(thesisId).orElseThrow(() -> new ThesisDoesNotExistException(String.valueOf(thesisId)));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String formattedDateTime = null;
        if (thesis.getPresentation() != null && thesis.getPresentation().getPresentationStartTime() != null) {
            formattedDateTime = thesis.getPresentation().getPresentationStartTime().format(formatter);
        }
        model.addAttribute("formattedDateTime", formattedDateTime);
        model.addAttribute("masterThesis", thesis);
        model.addAttribute("professors", professorService.findAllByProfessorStatus(true, false));
        model.addAttribute("members", professorService.findAllByProfessorStatus(true, true));
        model.addAttribute("rooms", roomService.findAll());
        return "editMasterThesis";
    }

    @PostMapping("/update/{thesisId}")
    public String updateMasterThesis(@PathVariable Long thesisId,
                                     @RequestParam(required = false) String note,
                                     @ModelAttribute MasterThesis masterThesis) {
        MasterThesis thesis = masterThesisService.findThesisById(thesisId).orElseThrow(() -> new ThesisDoesNotExistException(String.valueOf(thesisId)));
        if (!thesis.equals(masterThesis)) {
            masterThesisService.updateMasterThesis(thesisId, masterThesis);
            masterThesisStatusChangeService.addEditStatus(thesis, note, userService.getUser());

        }
        return String.format("redirect:/admin/details/%d", thesisId);
    }

    @PostMapping("/cancel/{thesisId}")
    public String cancelMasterThesis(@PathVariable Long thesisId, @RequestParam(required = false) String note,
                                     @RequestParam Long statusId) {
        MasterThesis thesis = masterThesisService.findThesisById(thesisId).orElseThrow(() -> new ThesisDoesNotExistException(String.valueOf(thesisId)));
        masterThesisStatusChangeService.updateAndCancelStatus(statusId, thesis, note, userService.getUser(), false);
        masterThesisService.updateStatus(thesisId, MasterThesisStatus.CANCELLED);
        return String.format("redirect:/admin/list-masters", thesisId);
    }

    @PostMapping("/grade/{statusId}")
    public String setGrade(@PathVariable Long statusId,
                                @RequestParam Integer grade,
                                @RequestParam Long thesisId,
                                @RequestParam(required = false) String note) {
        try {
            MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();

            masterThesisStatusChangeService.updateLastStatus(statusId, masterThesis, note, userService.getUser(), true);
            masterThesisService.updateStatus(thesisId, masterThesis.getStatus().getNextStatusFromCurrent());
            masterThesisService.addGrade(thesisId, grade);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return String.format("redirect:/admin/details/%d", thesisId);
    }

}

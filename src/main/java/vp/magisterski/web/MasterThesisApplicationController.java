package vp.magisterski.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.model.enumerations.MasterThesisDocumentType;
import vp.magisterski.model.enumerations.MasterThesisStatus;
import vp.magisterski.service.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/applicationForm")
public class MasterThesisApplicationController {

    private final UserService userService;
    private final ProfessorService professorService;
    private final MasterThesisService masterThesisService;
    private final MasterThesisDocumentService masterThesisDocumentService;
    private final MasterThesisStatusChangeService masterThesisStatusChangeService;

    public MasterThesisApplicationController(UserService userService, ProfessorService professorService, MasterThesisService masterThesisService, MasterThesisDocumentService masterThesisDocumentService, MasterThesisStatusChangeService masterThesisStatusChangeService) {
        this.userService = userService;
        this.professorService = professorService;
        this.masterThesisService = masterThesisService;
        this.masterThesisDocumentService = masterThesisDocumentService;
        this.masterThesisStatusChangeService = masterThesisStatusChangeService;
    }

    @ModelAttribute
    public void trackUsername(Model model) {
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }

    @GetMapping("/newMasterThesisApplicationForm")
    public String getHomePage(Model model) {
        model.addAttribute("professors", professorService.findAllByProfessorStatus(true, false));
        return "masterThesisApplication";
    }

    @PostMapping("/newMasterThesisApplicationForm")
    public String saveNewMasterThesis(@RequestParam String mentor,
                                      @RequestParam String title,
                                      @RequestParam("fileInput1") MultipartFile fileInput1,
                                      @RequestParam("fileInput2") MultipartFile fileInput2,
                                      @RequestParam("fileInput3") MultipartFile fileInput3
    ) {
        try {
            MasterThesis thesis = masterThesisService.newThesis("201163", title, mentor);
            masterThesisDocumentService.saveFile(thesis, MasterThesisDocumentType.THESIS_JUSTIFICATION, fileInput1);
            masterThesisDocumentService.saveFile(thesis, MasterThesisDocumentType.PLAN_AND_LITERATURE_REVIEW, fileInput2);
            masterThesisDocumentService.saveFile(thesis, MasterThesisDocumentType.STUDENT_BIOGRAPHY, fileInput3);
            masterThesisStatusChangeService.addStatus(thesis, MasterThesisStatus.STUDENT_THESIS_REGISTRATION, LocalDateTime.now(), true);
            masterThesisStatusChangeService.addStatus(thesis, MasterThesisStatus.MENTOR_VALIDATION);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/";
    }

}

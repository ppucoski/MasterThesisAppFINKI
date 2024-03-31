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

import java.util.ArrayList;
import java.util.List;

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
    public String findAllThesis(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "2") int size,
                                Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        model.addAttribute("MasterThesis", masterThesisService.findAllByStatusOrderGreaterThan(masterThesisToShow(), pageable));
        return "MasterThesisList";
    }

}

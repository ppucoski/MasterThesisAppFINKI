package vp.magisterski.web;


import org.bouncycastle.math.raw.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vp.magisterski.model.magister.MasterThesis;
import vp.magisterski.service.MasterThesisService;
import vp.magisterski.service.UserService;

import java.util.List;

@Controller
@RequestMapping(  "/masterThesis")
public class MasterThesisController {
    private final UserService userService;
    private final MasterThesisService masterThesisService;
    public MasterThesisController(UserService userService, MasterThesisService masterThesisService) {
        this.userService = userService;
        this.masterThesisService = masterThesisService;
    }


    @ModelAttribute
    public void trackUsername(Model model){
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }


    @GetMapping("/masterThesisInfo")
    public String getMasterThesisInfo(Model model){
        List<MasterThesis> thesis = masterThesisService.findByStudentIndex("201163");
        model.addAttribute("thesis", thesis);
        return "masterThesisInfo";
    }

    @GetMapping("/details/{thesisId}")
    public String details(Model model, @PathVariable Long thesisId){
        MasterThesis masterThesis = masterThesisService.findThesisById(thesisId).get();
        model.addAttribute("thesis", masterThesis);
        return "masterThesisDetails";
    }
}

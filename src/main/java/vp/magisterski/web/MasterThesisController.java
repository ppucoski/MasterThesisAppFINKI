package vp.magisterski.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import vp.magisterski.service.UserService;

@Controller
@RequestMapping(  "/masterThesis")
public class MasterThesisController {
    private final UserService userService;

    public MasterThesisController(UserService userService) {
        this.userService = userService;
    }


    @ModelAttribute
    public void trackUsername(Model model){
        String username = userService.getUsernameFromUser();
        model.addAttribute("user", username);
    }


    @GetMapping("/masterThesisInfo")
    public String getMasterThesisInfo(){
        return "masterThesisInfo";
    }
}

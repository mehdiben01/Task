package AdminController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class StaffController {

    @GetMapping("/staff")
    public String getStaff(){
        return "tache";
    }
}

package pt.ipportalegre.estgd.studentmonitoringsystem.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/api/v1/hello")
public class HelloController {
    @GetMapping(path = "")
    public @ResponseBody String hello() {
        return "API says hello";
    }
}

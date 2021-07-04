package com.devopsbuddy.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Santa Cecilia
 */

@Controller
public class CopyController {
    
    @RequestMapping("/about")
    public String about(){
        return "copy/about";
    }
    
}

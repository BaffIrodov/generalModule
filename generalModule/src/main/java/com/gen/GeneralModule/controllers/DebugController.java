package com.gen.GeneralModule.controllers;

import com.gen.GeneralModule.services.DebugService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/debug")
@Log4j2
public class DebugController {
    @Autowired
    DebugService debugService;

    @GetMapping("/results-link-processed/{value}")
    public void setResultsLingProcessed(@PathVariable Boolean value) {
        debugService.setResultsLinkProcessed(value);
    }
}

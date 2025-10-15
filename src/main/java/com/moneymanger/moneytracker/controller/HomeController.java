package com.moneymanger.moneytracker.controller;


import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/status","/health"})
public class HomeController {



    @GetMapping("/")
    public  String HeathCheckup()
    {
        return  "Application is Running";
    }


}

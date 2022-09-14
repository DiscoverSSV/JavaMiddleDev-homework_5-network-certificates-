package ru.digitalhabits.homework5.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.digitalhabits.homework5.model.Hello;

@RestController
public class HelloController {

    @RequestMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public Hello hello() {
        return new Hello().setMessage("Hello! I'am homework number five!");
    }

}

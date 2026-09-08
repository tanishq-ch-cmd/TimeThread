package com.tanishqchcmd.timethread;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
        @RestController
public class HelloController
{
    @GetMapping("/")
    public String sayHello() {
        return "Hello, TimeThread!";}
    @GetMapping("/meow")
    String sayMeow(){

        {
            return"Meow meow";
        }
    }
}

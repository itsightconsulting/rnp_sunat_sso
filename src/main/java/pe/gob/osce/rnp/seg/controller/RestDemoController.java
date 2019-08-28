package pe.gob.osce.rnp.seg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class RestDemoController {

    @GetMapping("/getURL")
    public @ResponseBody String getURL(HttpServletRequest request){
        return request.getRequestURL().toString();
    }
}

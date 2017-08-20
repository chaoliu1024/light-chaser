package group.chaoliu.lightchaser.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HelloWorldController {

    @RequestMapping("/hello")
    public String hello(@RequestParam(value = "name", required = false, defaultValue = "sss")
                                String name, Model model) {

        List<Map> result = new ArrayList<>();

        result.add(new HashMap<String, String>() {
            {
                put("name", "zhangsan");
            }
        });
        result.add(new HashMap<String, String>() {
            {
                put("name", "lisi");
            }
        });
        result.add(new HashMap<String, String>() {
            {
                put("name", "wanger");
            }
        });

        model.addAttribute("result", result);
        model.addAttribute("name", name);

        return "helloworld";
    }


    @RequestMapping(value = "/json", method = RequestMethod.GET)
    @ResponseBody
    public Map jsonResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("num", "222");
        map.put("name", "tt");
        return map;
    }
}
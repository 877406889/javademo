package com.mmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 87740
 * @data 2019/7/12 14:52
 */
@Controller
@RequestMapping("/test/")
public class TestController {
    @RequestMapping("cout.do")
    @ResponseBody
    public String test(){
        System.out.println("这只是测试了以下ok？");
        String str="确实这只是测试了一下";
        return str;
    }
}

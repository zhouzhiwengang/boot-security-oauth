package com.zzg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/userIndex")
public class IndexController {
	@RequestMapping("/index")
	public String index() {
		return "indexs";
	}
}

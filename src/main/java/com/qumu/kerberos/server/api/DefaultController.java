package com.qumu.kerberos.server.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

	@RequestMapping("/hello")
	public ResponseEntity<String> sayHello() {
		return new ResponseEntity<String>("Hello, client", HttpStatus.OK);
	}
	
	@RequestMapping(value = "/status", produces="application/json") 
	public ResponseEntity<Map<String,String>> status() {
		Map<String,String> json = new HashMap<>();
		json.put("status", "OK");
		return new ResponseEntity<>(json, HttpStatus.OK);
	}
}

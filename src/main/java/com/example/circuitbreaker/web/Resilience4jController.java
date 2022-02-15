package com.example.circuitbreaker.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.circuitbreaker.service.TestService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/testSCCB1")
public class Resilience4jController {
	
	private final Logger log = LoggerFactory.getLogger(Resilience4jController.class);
	
	@Autowired
	public TestService testService;
	
	
	@GetMapping("/fail/{param}")    
    public Mono<String> fail(@PathVariable String param) {
    	return testService.fail(param);

    }
	
	@GetMapping("/success/{param}")    
    public Mono<String> success(@PathVariable String param) {
    	return testService.success(param);

    }
	
	@GetMapping("/{param}")
	public Mono<String> testSCCB(
    		@PathVariable String param,
    		@RequestHeader HttpHeaders headers,
    		@CookieValue(name = "httpclient-type", required=false, defaultValue="undefined") String httpClientType) {
    	
    	
    	log.info(">>>> Cookie 'httpclient-type={}'", httpClientType);

		headers.forEach((key, value) -> {
			log.info(String.format(">>>>> Header '%s' => %s", key, value));
		});
		
        log.info("### Received: /testSCCB/" + param);
		
		String msg = "("+headers.get("host")+":"+")"+param + " => Working successfully !!!";
		log.info("### Sent: " + msg);
		
		return testService.testSCCB(param);

	}

}

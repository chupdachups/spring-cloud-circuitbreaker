package com.example.circuitbreaker.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@RestController
public class CircuitBreakerController {

private final Logger log = LoggerFactory.getLogger(CircuitBreakerController.class);
    
	@Value("${server.port}")
	int port;

    // filter로 SCL에서 제공하는 ReactorLoadBalancerExchangeFilterFunction을 지정
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    CircuitBreakerController(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }

    @CircuitBreaker(name = "testsccb", fallbackMethod = "sccbFallback")
    @GetMapping("/testsclb1")    
    public Mono<String> testSCLB1() {
        WebClient client = WebClient.builder()
            .filter(this.lbFunction)
            .baseUrl("http://webserver") //baseUrl은 http://{Service ID}로 지정
            .build();

        return client.get()
            .uri("/webclient/test SCLB1")
            .retrieve()
            .bodyToMono(String.class);
    }
    
    @CircuitBreaker(name = "testsccb", fallbackMethod = "sccbFallback")
    @GetMapping("/testSCCB/{param}")    
    public Mono<String> testSCCB(
    		@PathVariable String param,
    		@RequestHeader HttpHeaders headers,
    		@CookieValue(name = "httpclient-type", required=false, defaultValue="undefined") String httpClientType) {
    	
    	log.info(">>>> Cookie 'httpclient-type={}'", httpClientType);

		headers.forEach((key, value) -> {
			log.info(String.format(">>>>> Header '%s' => %s", key, value));
		});
		
        log.info("### Received: /testSCCB/" + param);
		
		String msg = "("+headers.get("host")+":"+port+")"+param + " => Working successfully !!!";
		log.info("### Sent: " + msg);
		
        WebClient client = WebClient.builder()
            .filter(this.lbFunction)
            .baseUrl("http://webserver") //baseUrl은 http://{Service ID}로 지정
            .build();

        return client.get()
            .uri("/testSCCB/"+ param)
            .retrieve()
            .bodyToMono(String.class);
    	
    }
    
    private Mono<String> sccbFallback(Throwable t) {
        return Mono.just("fallback invoked! exception type : " + t.getClass());
    }
    
    private Mono<String> sccbFallback(String param, HttpHeaders headers, String httpClientType, Throwable t) {
        return Mono.just("fallback invoked! exception type : " + t.getClass());
    }
    
}

package com.example.circuitbreaker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import reactor.core.publisher.Mono;

@Service
public class TestService {
	
	private final Logger log = LoggerFactory.getLogger(TestService.class);

	
	private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

	TestService(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }
	
	@CircuitBreaker(name = "testsccb", fallbackMethod = "sccbFallback")
    public Mono<String> fail(String param) {

    	return Mono.error(new Exception());
    }
	
	@CircuitBreaker(name = "testsccb", fallbackMethod = "sccbFallback")
    public Mono<String> success(String param) {

        return Mono.just(">>>>>>>>>>>> Success <<<<<<<<<<<<<");
    }
	
	@CircuitBreaker(name = "testsccb", fallbackMethod = "sccbFallback") 
    public Mono<String> testSCCB(String param) {
    	
        log.info("### Received: /testSCCB/" + param);

        WebClient client = WebClient.builder()
            .filter(this.lbFunction)
            .baseUrl("http://webserver") //baseUrl은 http://{Service ID}로 지정
            .build();

        return client.get()
            .uri("/testSCCB/"+ param)
            .retrieve()
            .bodyToMono(String.class);
    	
    }
	
	private Mono<String> sccbFallback(String param, Throwable t) {
        return Mono.just("fallback invoked! exception type : " + t.getClass());
    }
	
}

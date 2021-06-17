package com.cipley.sample.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.stereotype.Service;

import com.cipley.sample.adapter.SampleAdapter;
import com.cipley.sample.model.ResponseModel;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class SampleService {

	@Autowired
	private SampleAdapter adapter;
	
	@Autowired
	private ReactiveCircuitBreaker sampleCircuitBreaker;
	
	public Mono<ResponseModel> executeService(String param) {
		return sampleCircuitBreaker.run(adapter.call(param)
				, fallback -> {
					//fallback
					log.warn("Fallback Triggered");
					return Mono.just(new ResponseModel(200, "Fallback"));
				});
	}
	
}

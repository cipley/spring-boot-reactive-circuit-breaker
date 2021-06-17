package com.cipley.sample.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.cipley.sample.model.ResponseModel;

import reactor.core.publisher.Mono;

@Component
public class SampleAdapter {

	@Value("${scheme}")
	private String scheme;
	
	@Value("${host}")
	private String host;
	
	@Value("${port}")
	private int port;
	
	@Autowired
	private WebClient webClient;
	
	public Mono<ResponseModel> call(String request) {
		return webClient.get()
				.uri(builder -> builder
						.scheme(scheme)
						.host(host)
						.port(port)
						.queryParam("request", request)
						.build())
				.retrieve()
				.bodyToMono(ResponseModel.class);
	}
	
}

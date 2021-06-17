package com.cipley.sample.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.cipley.sample.model.ResponseModel;
import com.cipley.sample.model.exception.BadRequestException;
import com.cipley.sample.model.exception.ServiceUnavailableException;

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
				.onStatus(HttpStatus::is4xxClientError, clientError -> {
					return clientError.bodyToMono(ResponseModel.class)
							.flatMap(error -> Mono.error(new BadRequestException(Short.valueOf(String.valueOf(error.getStatus())), 
									error.getMessage())));
				})
				.onStatus(HttpStatus::is5xxServerError, serverError -> {
					return serverError.bodyToMono(ResponseModel.class)
							.flatMap(error -> Mono.error(new ServiceUnavailableException(Short.valueOf(String.valueOf(error.getStatus())), 
									error.getMessage())));
				})
				.bodyToMono(ResponseModel.class);
	}
	
}

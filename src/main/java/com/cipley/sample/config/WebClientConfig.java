package com.cipley.sample.config;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
public class WebClientConfig {

	@Value("${connect-timeout.ms:3000}")
	private int connectTimeout;
	
	@Value("${socket-timeout.ms:3000}")
	private int socketTimeout;
	
	@Value("${wait-timeout.ms:3000}")
	private int waitTimeout;
	
	@Value("${max-total-conn:20}")
	private int maxTotalConn;
	
	@Value("${idle-timeout.ms:3000}")
	private int idleTimeout;
	
	private final ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
	
	@Bean
	public WebClient webClient() {
		ExchangeStrategies strategy = ExchangeStrategies.builder()
				.codecs(configurer -> {
					configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(mapper));
					configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(mapper));
				})
				.build();
		return WebClient.builder()
				.exchangeStrategies(strategy)
				.clientConnector(new ReactorClientHttpConnector(httpClient()))
				.build();
	}
	
	private HttpClient httpClient() {
		ConnectionProvider connProvider = ConnectionProvider.builder("connectionPool")
				.maxConnections(maxTotalConn)
				.maxIdleTime(Duration.ofMillis(idleTimeout))
				.evictInBackground(Duration.ofMillis(idleTimeout))
				.pendingAcquireTimeout(Duration.ofMillis(waitTimeout))
				.build();
		return HttpClient.create(connProvider)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
				.keepAlive(false)
				.doOnConnected(connection -> {
					connection.addHandlerLast(new ReadTimeoutHandler(socketTimeout, TimeUnit.MILLISECONDS))
							.addHandlerLast(new WriteTimeoutHandler(connectTimeout, TimeUnit.MILLISECONDS));
				});
	}
	
}

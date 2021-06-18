package com.cipley.sample.test;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cipley.sample.TestApplication;
import com.cipley.sample.model.ResponseModel;
import com.cipley.sample.service.SampleService;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class SampleCircuitBreakerTest {

	public static MockWebServer sampleMock;
	
	@Autowired
	private SampleService service;
	
	@BeforeAll
	static void startMockServer() throws IOException {
		sampleMock = new MockWebServer();
		sampleMock.start(9000);
	}
	
	@AfterAll
	static void stopMockServer() throws IOException {
		sampleMock.shutdown();
	}
	
	@Test
	void ignoreExceptionTest() {
		sampleMock.enqueue(new MockResponse()
				.setResponseCode(400)
				.addHeader("Content-Type", "application/json")
				.setBody("{\"status\":400, \"message\":\"Bad Request\"}"));
		Mono<ResponseModel> testResponse = service.executeService("test");
		
		StepVerifier.create(testResponse)
				.expectNextMatches(response -> response.getStatus() == 200)
				.verifyComplete();
	}
	
	@Test
	void handleExceptionTest() {
		sampleMock.enqueue(new MockResponse()
				.setResponseCode(500)
				.addHeader("Content-Type", "application/json")
				.setBody("{\"status\":500, \"message\":\"Internal Server Error\"}"));
		Mono<ResponseModel> testResponse = service.executeService("test1");
		
		StepVerifier.create(testResponse)
				.expectNextMatches(response -> response.getStatus() == 200)
				.verifyComplete();
	}
	
	@Test
	void timeoutTest() {
		sampleMock.enqueue(new MockResponse()
				.addHeader("Content-Type", "application/json")
				.setSocketPolicy(SocketPolicy.NO_RESPONSE));
		
		Mono<ResponseModel> testResponse = service.executeService("test2");
		
		StepVerifier.create(testResponse)
				.expectNextMatches(response -> response.getStatus() == 200)
				.verifyComplete();
	}
	
}

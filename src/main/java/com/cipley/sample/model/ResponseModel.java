package com.cipley.sample.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseModel implements Serializable {
	private static final long serialVersionUID = 8465473128617253554L;

	private int status;
	private String message;
	
}

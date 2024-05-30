package com.readinessbtpnbe.orderBE.dto.response;

import lombok.Data;

@Data
public class ResponseBodyDTO {
	private long total;
	private Object data;
	private String message;
	private int statusCode;
	private String status;
}

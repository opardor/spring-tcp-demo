package com.poc.tcpserver;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class HandlerServiceImpl implements HandlerService {

	@Override
	public String processMessage(String message) {
		log.info("Received message: {}", message);
		return message + " received";
	}

}
package com.poc.tcpclient;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class MessageServiceImpl implements MessageService {

	private TcpClientGateway tcpClientGateway;

	@Autowired
	public MessageServiceImpl(TcpClientGateway tcpClientGateway) {
		this.tcpClientGateway = tcpClientGateway;
	}

	public void sendMessage() {
		String message = LocalDateTime.now().toString();
		log.info("Send message: {}", message);
		String response = tcpClientGateway.send(message);
		log.info("Response received: {}", response);
	}

	@Override
	public String sendMessage(String message) {
		log.info("Sending message: {}", message);
		String response = tcpClientGateway.send(message);
		return "response: " + response;
	}
}
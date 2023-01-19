package com.poc.tcpserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class TcpServerEndpoint {

	private HandlerService messageService;

	@Autowired
	public TcpServerEndpoint(HandlerService messageService) {
		this.messageService = messageService;
	}

	@ServiceActivator(inputChannel = "inboundChannel")
	public String process(String message) {
		return messageService.processMessage(message);
	}
}
package com.poc.tcpclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MessageJobScheduler {

	private MessageService messageService;

	@Autowired
	public MessageJobScheduler(MessageService messageService) {
		this.messageService = messageService;
	}

	@Scheduled(initialDelay = 1000L, fixedDelay = 4000L)
	public void sendMessageJob() {
		messageService.sendMessage("TestPOC");
	}

}
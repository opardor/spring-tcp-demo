package com.poc.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnection;
import org.springframework.integration.ip.tcp.connection.TcpConnectionOpenEvent;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class TcpServerConfig implements ApplicationEventPublisherAware {

	@SuppressWarnings("unused")
	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Bean
	public AbstractServerConnectionFactory serverConnectionFactory() {
		TcpNetServerConnectionFactory connectionFactory = new TcpNetServerConnectionFactory(9098);
		return connectionFactory;
	}

	@Bean
	public MessageChannel inboundChannel() {
		return new DirectChannel();
	}

	@Bean
	public TcpInboundGateway inboundGateway(AbstractServerConnectionFactory serverConnectionFactory,
			MessageChannel inboundChannel) {
		TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
		tcpInboundGateway.setConnectionFactory(serverConnectionFactory);
		tcpInboundGateway.setRequestChannel(inboundChannel);
		return tcpInboundGateway;
	}

	@EventListener(TcpConnectionOpenEvent.class)
	@SneakyThrows
	void connectionOpen(TcpConnectionOpenEvent event) {
		TcpConnection connection = (TcpConnection) event.getSource();
		if (connection.isServer()) {
			log.info("Sending delayed hello to client.");
			Thread.sleep(25000);
			connection.send(MessageBuilder.withPayload("Hello").build());
		}
	}

}
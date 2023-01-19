package com.poc.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpConnectionInterceptorFactoryChain;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.poc.HelloWorldInterceptorFactory;

@Configuration
@EnableScheduling
public class TcpClientConfig implements ApplicationEventPublisherAware {

	private ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Bean(destroyMethod = "stop")
	public AbstractClientConnectionFactory clientConnectionFactory() {
		TcpNetClientConnectionFactory connectionFactory = new TcpNetClientConnectionFactory("localhost", 9098);
		connectionFactory.setApplicationEventPublisher(applicationEventPublisher);
		connectionFactory.setSingleUse(false);
		connectionFactory.setInterceptorFactoryChain(interceptorFactory());
		return connectionFactory;
	}

	@Bean
	public MessageChannel outboundChannel() {
		return new DirectChannel();
	}

	@Bean
	@ServiceActivator(inputChannel = "outboundChannel")
	public MessageHandler outboundGateway(AbstractClientConnectionFactory clientConnectionFactory) {
		TcpOutboundGateway tcpOutboundGateway = new TcpOutboundGateway();
		tcpOutboundGateway.setConnectionFactory(clientConnectionFactory);
		tcpOutboundGateway.setAsync(true);
		return tcpOutboundGateway;
	}

	@Bean
	public TcpConnectionInterceptorFactoryChain interceptorFactory() {
		HelloWorldInterceptorFactory helloFactory = new HelloWorldInterceptorFactory();
		helloFactory.setApplicationEventPublisher(applicationEventPublisher);

		TcpConnectionInterceptorFactoryChain factoryChain = new TcpConnectionInterceptorFactoryChain();
		factoryChain.setInterceptor(helloFactory);
		return factoryChain;
	}

	@EventListener
	public void onStartup(ContextRefreshedEvent event) throws InterruptedException {
		clientConnectionFactory().getConnection();
	}

}
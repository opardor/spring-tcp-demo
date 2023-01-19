package com.poc;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.ip.tcp.connection.TcpConnectionInterceptorSupport;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;

/**
 * @author Gary Russell
 * @since 2.0
 *
 */
public class HelloWorldInterceptor extends TcpConnectionInterceptorSupport {

	private volatile boolean negotiated;

	private final Semaphore negotiationSemaphore = new Semaphore(0);

	private volatile long timeout = 9000;

	private volatile String hello = "Hello";

	private volatile String world = "world!";

	private volatile boolean closeReceived;

	private volatile boolean pendingSend;

	public HelloWorldInterceptor() {
	}

	public HelloWorldInterceptor(String hello, String world, ApplicationEventPublisher applicationEventPublisher) {
		super(applicationEventPublisher);
		this.hello = hello;
		this.world = world;
	}

	@Override
	public boolean onMessage(Message<?> message) {
		if (!this.negotiated) {
			synchronized (this) {
				if (!this.negotiated) {
					String payload = new String((byte[]) message.getPayload());
					logger.info(this.toString() + " received " + payload);
					if (!this.isServer()) {
						if (payload.equals(hello)) {
							try {
								logger.info(this.toString() + " received " + payload);
								this.negotiated = true;
								this.negotiationSemaphore.release();
								return true;
							} catch (Exception e) {
								throw new MessagingException("Negotiation error", e);
							}
						} else {
							throw new MessagingException(
									"Server Negotiation error, expected '" + hello + "' received '" + payload + "'");
						}
					}
				}
			}
		}
		try {
			return super.onMessage(message);
		} finally {
			// on the server side, we don't want to close if we are expecting a response
			if (!(this.isServer() && this.hasRealSender()) && !this.pendingSend) {
				this.checkDeferredClose();
			}
		}
	}

	@Override
	public void send(Message<?> message) {
		this.pendingSend = true;
		try {
			if (!this.negotiated) {
				if (!this.isServer()) {
					logger.info(this + " Waiting " + hello + " from server");
					try {
						this.negotiationSemaphore.tryAcquire(this.timeout, TimeUnit.MILLISECONDS);
					} catch (@SuppressWarnings("unused") InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					if (!this.negotiated) {
						throw new MessagingException("Client, Negotiation error. Hello expected");
					}
				}
			}
			super.send(message);
		} finally {
			this.pendingSend = false;
			this.checkDeferredClose();
		}
	}

	/**
	 * Defer the close until we've actually sent the data after negotiation
	 */
	@Override
	public void close() {
		if (this.negotiated && !this.pendingSend) {
			super.close();
			return;
		}
		closeReceived = true;
		logger.debug("Deferring close");
	}

	/**
	 * Execute the close, if deferred
	 */
	private void checkDeferredClose() {
		if (this.closeReceived) {
			logger.debug("Executing deferred close");
			this.close();
		}
	}

	@Override
	public String toString() {
		return "HelloWorldInterceptor [negotiated=" + negotiated + ", hello=" + hello + ", world=" + world
				+ ", closeReceived=" + closeReceived + ", pendingSend=" + pendingSend + "]";
	}
}
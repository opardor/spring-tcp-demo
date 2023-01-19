# spring-tcp-demo
Handshake started by server demo.
 
Using TcpOubbountGateway and TcpInboudGateway.

An issue was detected in TcpOubbountGateway when client don't receive the "hello" from server and throws and exception. Sending subsecuent messages got blocked.

Issue reported [https://github.com/spring-projects/spring-integration/issues/3993](https://github.com/spring-projects/spring-integration/issues/3993)

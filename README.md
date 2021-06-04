# gRPC Chat

### Description

Implementation of a peer-to-peer chat with gRPC in Kotlin.
User could act as a client or server. If the application runs as a server, the user must provide his own IP address and port.
Otherwise, the IP and port of a host should be specified and server should be already running.
If some error occurs (i.e. server IP is invalid), the application will handle it, show an alert and open the initialization window.

Application uses MVC-like architecture - but since there is no actual "data", there aren't any models, only grpc-chat-service which
transfers the messages.
Controller handles server / client instances creation, it also manages channels with received and sent messages. 
Views provide with basic UI, and also are able to trigger actions in controller originating from pressed buttons, for example.
Controller's instances are injected to the views using TornadoFX dependency injection framework.

Chat service has one bidirectional streaming RPC `chat` with both request and response stream of `ChatMessage` messages.
Application uses Kotlin `Flow` objects to represent asynchronous streams.

### Build & Run

To generate java classes, services and client stubs:

```
./gradlew generateProto
```

To build whole project:

```
./gradlew build
```

To run:

```
./gradlew run
```

### Authors

Oleg Smirnov, Alexey Shaposhnikov, Egor Gordienko
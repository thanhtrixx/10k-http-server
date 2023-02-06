# Introduction

This project demonstrates how to use Netty and Kotlin coroutines to handle thousands of requests efficiently and effectively.

- Netty is a powerful, high-performance networking framework
- Kotlin Coroutines provide a convenient way to handle asynchronous tasks and write performant, concurrent code.

This project follows the principles outlined in the article ["Thousands of Socket Connections in Java: Practical Guide"](https://dzone.com/articles/thousands-of-socket-connections-in-java-practical) by DZone.

# Prerequisites

- Basic knowledge of Netty and Kotlin coroutines
- A development environment with the latest version of Kotlin and the necessary dependencies installed
- Understanding of the principles outlined in the article ["Thousands of Socket Connections in Java: Practical Guide"](https://dzone.com/articles/thousands-of-socket-connections-in-java-practical) by DZone
- System setup to increase limits on sockets, as explained in the StackOverflow article [How to increase limits on sockets on OSX for load testing](https://stackoverflow.com/a/7580233/544742).
- [K6](https://k6.io/) installed for benchmarking purposes

# Features

- A Netty server that listens for incoming requests on port 8080
- Coroutine channels to manage the flow of requests and responses between the server and the coroutines handling the requests
- Use of coroutines to handle requests asynchronously and process them concurrently
- Implementation of the best practices outlined in the article ["Thousands of Socket Connections in Java: Practical Guide"](https://dzone.com/articles/thousands-of-socket-connections-in-java-practical) by DZone
- Basic error handling and logging
- Benchmarking using K6 to measure the performance of the server under different load scenarios

# Getting Started

1. Setup following [How to increase limits on sockets on OSX for load testing](https://stackoverflow.com/a/7580233/544742)
2. Clone the repository
3. Build `./gradlew clean build`
4. Execute `java -jar build/libs/10k-http-server*.jar`
5. Open other terminal and execute `k6 run k6/script.js`

# Conclusion

This project serves as a demonstration of using Netty and Kotlin coroutines to handle a high volume of requests efficiently. By following the principles outlined in the article "Thousands of Socket Connections in Java: Practical Guide" by DZone, it provides a practical example of how to build a scalable and performant server using these technologies. The basic benchmarking setup using K6 allows you to assess the performance of the server under various load conditions.

Please note that this project is not intended for use in a production environment, but rather as a starting point for further development. Additional error handling, security measures, and performance optimization techniques may be necessary, depending on the requirements of your use case.

Additionally, this project serves as a foundation for building a long-polling project, making it an essential resource for anyone looking to explore the capabilities of Netty and Kotlin coroutines.


This is the server solution of following training task.
Client is here https://github.com/kosbr/ix-client

## Task description
Create client and server which interact with the specific protocol. 
The goal is primitive call of server remote methods.
 
### Protocol description

The protocol has commands and results. A client sends a command to the server and the server sends back the result.
Every command must have unique ID in the session. The result must contain this number. A client is responsible for 
generation of these id numbers.

### Command & Result

A command must have following values:

* Id number (unique)
* Service name
* Method name
* List of arguments

A result must have:

* Id number
* The result object

Id number is needed for possibility of calling several methods at the same time in one TCP connection. In other words
it must be possible to call one method and immediately call another method. Both methods must work in parallel and the
first result must belong to a command which method have finished first.

### Server part
It must handle requests from several clients simultaneously. All income commands and results must 
be logged (Object.toString()). The release of resources occurs when a client disconnects.

Only one thread should be used for accepting clients. One thread per client should be used for getting commands.
Command handling must be performed with pool of threads (one for all application). 
 
### Client part

Opens TCP connection with server and sends commands and handles answers. It can be done simultaneously in
  several threads. All commands and results should be logged.

### Demands
* log4j (http://jakarta.apache.org). 
* Client must be able to create a situation with simultaneously calling of several methods.
* Server must read a port for listening from command line
    For example:

```
    java ­cp classes com.mytest.MyServer 2323 
```
 
* Server must read config file while starting. The structure is below:
```
    <ServiceName>=<ClassName> 
    <ServiceName>=<ClassName> 
    .... 
 ```
 
 While starting server must create one object from ClassName per one Service. It must provide access to this object by ServiceName.
 
### Simple example

Imagine we have some class like this:

```
public class SomeService {

    public String toUpper(String str) {
        return str.toUpperCase();
    }
    
}
```
 
 and configuration file:
 
```
service.some=ru.kos.ix.service.SomeService
```
 
If a client sends a command with following parameters:
* id=14
* service = service.some
* method = toUpper
* arguments = 'hello'

A server must return result with parameters:
* id=14
* answer = 'HELLO'
 
## Server Implementation description

Main thread launches server (start listening to a port). After this AcceptThread is started for client
accepting. The major part of time it is blocked on accept method. When a client connects AcceptThread
creates a unique client id and new thread: ClientThread. One ClientThread corresponds to one client.

The main action of ClientThread is waiting for a request from client. After client sends a request with 
command it takes a thread from common pool of threads and delegate it command handling. So it doesn't block
and able to get more requests. After command handling is finished, the thread that have done it sends 
result back to client. A result also can be an error if something is wrong.
 
![Threads](threads.jpg) 
 
This architecture allows client to send many requests without waiting for answers. Answers come back to client
in order that doesn't equal to sending order. Result id allows to find out the command which this result 
corresponds to. 

It must be mentioned about server stopping. It is not correct just kill it because all sockets should be 
closed before. That's why every 0.5 seconds Main thread reads special file (file name is written in 
server.properties). It expects to read line like 'active=true'. In other case it closes all sockets and 
 stops server. So before launching this file must have this line. To stop server just remove it from the 
 file.

The last thing must be described here is method searching in class. If there are no arguments with null,
it is easy to find it with standard instruments. If it has null argument, we don't know its type, so we 
have to look throw all similar methods and find out if it will be launched with given number of arguments. 
'Similar methods' means methods with the same name and arguments number. If there are more than one such 
methods an error will be returned to a client.
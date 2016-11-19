# Multithreading training task
This is the server solution of training task that is described below.

* The server part description of the solution is [here][desc]. 
* How to launch? Instruction is [here][launch].
* Client implementation and description is [here][client].

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
 
[client]: https://github.com/kosbr/ix-client 
[desc]: https://github.com/kosbr/ix-server/blob/master/description.md
[launch]: https://github.com/kosbr/ix-server/blob/master/launch.md
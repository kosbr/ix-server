## How to launch

1)Clone client (https://github.com/kosbr/ix-client)

2)Clone server (https://github.com/kosbr/ix-server)

3)Create file with one line:

```
active=true
```

4)Write this filename to server.properties file (server project). The example is below:

```
status.file=/home/kosbr/ix/status
```

5)Run server with parameter 3128

It can be done from IDE by running main method with parameter or if it is built in 
jar with command:

```
java -jar app.jar 3128
```

To build jar just run command:

```
gradle clean build
```

6)Start the client. I've done several main methods for different tests. Every method is proper.

Then client will connect to the server and perform the operations. Now 'localhost' and port 3128
is written in code. I've done it in this way because this is training application and it won't be
used in production. If server is hosted on other address or port it can be easily changed.

7)To stop server just change status file:

```
active=false
```

Do not forget to revert it before next start.
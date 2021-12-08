# crypto-chat

A local messaging service built to apply cryptographic security standards to a real world application. Makes use of symmetric and assymetric encryption, as well as certificates, signatures, HMAC, hashing, etc.

# Demo

1. Download server.jar, client.jar, and the openssl directory
2. Make sure that port 5505 is free for the demo server to use and all 3 downloaded files are in the same directory
3. 
```bash
java -jar server.jar
```
```bash
java -jar client.jar
```
5. In the client UI, type in the the local IP of the machine hosting the server (if same machine is running the server, use localhost as the IP
6. To create an account and log in, enter a username and password (at least 12 characters). This will be stored for future use and the same username cannot be created twice. If you would like to use the signed messages feature, you must log in with a username which corresponds to OpenSSL credentials. Two demo accounts have already been created:
* Username: shane       Password: p
* Username: lukas       Password: p
7. Send messages.
* By default, messages are broadcast to all clients connected to the server, but if you type another user's username in the box at the top, it will send your message privately to that user. If your certificates are set up, you can send signed messages verifying you as their sender by checking the "Signed mode" button.

CrococryptQuerl
===============
CrococryptQuerl (CQuerl) is a web-based open-source file encryption and file exchange tool.

If you trust the application service provider which is running CrococryptQuerl, it can be seen as an "anonymous & encrypted safe deposit box for computer files".

---INTRO---
The encryption is done on-the-fly using the user's password. Of course, this requires a trusted TLS/SSL connection to the server running CQuerl.

The user provides a file and password and receives an ID. No plaintext data is stored in CQuerl including filenames. If the user looses the ID or forgets the password, the encrypted file cannot be recovered - even by the service provider.

DOWNLOAD
========
* A demo running on Google's App Engine (GAE) can be found here:
  https://crococrypt-querl.appspot.com
  ==> GAE limits the files to be uploaded to 1MB(!). The file database will be cleared occasionally.
  ==> Crypto seems to be working slowly on GAE, so be patient! ;-)

* For a standalone binary package using Jetty 9 go to the release tab:
  https://github.com/fhissen/CrococryptQuerl/releases
  ==> ZIP archive for all platforms running Java 1.7++, simply start "run.sh" or "run.bat".

TECHNICAL BACKGROUND
====================
Cryptography:
CrococryptQuerl uses the Java technology. As cryptography providers Oracle (originally Sun) and Bouncy Castle (open-source) are used. The state-of-the-art algorithms that are used are AES-256 and PKCS #5 (PBKDF2) with a SHA512-based HMAC using 100000 iterations. (Additionally, a ZIP compression for storage space optimization is used.)

Responsive Design:
CQuerl has a responsive design, it runs in all desktop and mobile browsers including Windows, MacOS, Linux, Android, iOS (iPhone/iPad) and Windows Phone clients.

File storage:
CQuerl uses a simple interface to interact with a file storage though Java streams. The binary package simply uses the file system to store the encrypted files and headers. The default implementation checks for null-byte injections and file inclusions. The GAE demo uses Google Datastore API. Any database (or else) can be used: CQuerl is open-source, download the source code and write your own adapter!

Server:
CQuerl is implemented as a Java servlet, the binary package uses an embedded Jetty but any servlet container can be used. Moreover, the basic technology could also be used outside a server (e.g., as a command-line interface or else).

Any Java application server or container engine can be used to run CQuerl. Simply configure the servlet "org.crococryptquerl.web.servlet.Querler" and put the "crococryptquerl.jar" (see release tab) in the classpath. When deploying on an Internet server, a TLS/SSL connection should be mandatory!

LICENSE
=======
CrococryptQuerl is licensed under the GPLv3

---3RD-PARTY PRODUCTS---
The following 3rd-party software is used in compliance with their respective licenses.

All trademarks and software are the property of their respective owners.

 * Apache Commons Codec
   http://commons.apache.org/codec/
   
 * Apache Commons FileUpload
   http://commons.apache.org/proper/commons-fileupload/
   
 * Bouncy Castle
   http://www.bouncycastle.org/

 * Java and the Java Runtime from Oracle
   http://www.oracle.com/technetwork/java/javase/downloads/index.html

 * Jetty Web server
   http://eclipse.org/jetty/

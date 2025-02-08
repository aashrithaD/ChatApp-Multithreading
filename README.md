**Problem Statement:**

The Internet Chatting application facilitates real-time messaging and also transfer of files between two users by utilizing multi-threading.The application is designed to have two main components, a main(reading thread) and a writing thread which allows for simultaneous reading and writing through network sockets. 

**Team Members:**
1.Aashritha Reddy Donapati
2.Neeharika Chintalapudi

**Running the Application:**
1.Open your terminal or command prompt.
2.Navigate to the directory where ChatApp.java is located.
3.Compile the Java application:
   javac ChatApp.java
4.Run the application for the first user:
   java ChatApp
5.Repeat the compilation and launch steps on a different terminal or machine for the second user.

**Usage Instructions:**
1.Upon starting the application, each user will be prompted to enter their username.
2.After entering the username, the application will display the listening port number.
3.Users must enter the target port number to establish a connection:
   Enter target port number: <target_port>
4.Once the connection is established, users can start sending, revceiving messages and transferring files.

**Commands:**
1.Send a message: Type your message and press enter to send it to the connected user.
2.Send a file: Type Transfer <FILENAME> to send a file to the connected user.
3.Receive a file: Files are automatically handled by the application upon receiving a file transfer command from the other user.
4.Exit: Type Exit to close the connection and exit the application.
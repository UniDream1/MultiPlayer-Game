# ClientHanler.java
  
  this class extends Thread in order to handle a specific client connected to the back-end-Socket.
  For further details @see ClientHandler.java
 
# Server.java

  actual server listening for new user connections, if a socket is in bound, then it is passed to ClientHandler and simultaneously added to an ArrayList.
  
# Frame.java
  
  GUI for operating the Server

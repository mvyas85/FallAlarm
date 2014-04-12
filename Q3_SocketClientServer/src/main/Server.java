package main;



/*------------------------------------------------
Connection 1 received from: localhost
Got I/O streams
Sending message "Connection successful"
Client message: Thank you.
Transmission complete. Closing socket.
------------------------------------------------
// Fig. 16.3: Server.java
// Set up a Server that will receive a connection
// from a client, send a string to the client,
// and close the connection.
*/import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

class CloseWindowAndExit1 extends WindowAdapter {
   public void windowClosing( WindowEvent e )
   {
      System.exit( 0 );
   }
}

public class Server extends Frame {
   private TextArea display;

   public Server()
   {
      super( "Server" );
      display = new TextArea( "", 0, 0,
                    TextArea.SCROLLBARS_VERTICAL_ONLY );
      add( display, BorderLayout.CENTER );
      setSize( 300, 150 );
      setVisible( true );
   }

   public void runServer()
   {
      ServerSocket server;
      Socket connection;
      DataOutputStream output;
      DataInputStream input;
      int counter = 1;

      try {
         // Step 1: Create a ServerSocket.
         // Change the port number from 5000 to other number
         // if you encounter JVM_bind error
         // when running this program.
         server = new ServerSocket( 5000, 100 );

         while ( true ) {
            // Step 2: Wait for a connection.
            connection = server.accept();

            display.append( "Connection " + counter +
               " received from: " +
               connection.getInetAddress().getHostName() );

            // Step 3: Get input and output streams.
            input = new DataInputStream(
                        connection.getInputStream() );
            output = new DataOutputStream(
                         connection.getOutputStream() );
            display.append( "\nGot I/O streams\n" );

            // Step 4: Process connection.
            display.append(
               "Sending message \"Connection successful\"\n" );
            output.writeUTF( "Connection successful" );
            display.append( "Client message: " +
                                input.readUTF() );

            // Step 5: Close connection.
            display.append( "\nTransmission complete. " +
                            "Closing socket.\n\n" );
            connection.close();
            ++counter;
         }
      }
      catch ( IOException e ) {
         e.printStackTrace();
      }
   }
   public static void main( String args[] )
   {
      Server s = new Server();

      s.addWindowListener( new CloseWindowAndExit() );
      s.runServer();
   }
}
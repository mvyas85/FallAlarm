package main;

/*Client


-----------------------------------------------
Connected to: delllm
Got I/O Streams
Server message: Connection successful
Sending message "Thank you."
Transmission complete: Closing connection.
-----------------------------------------------
// Fig. 16.4: Client.java
// Set up a Client that will read information sent
// from a Server and display the information.
*/

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;

class CloseWindowAndExit extends WindowAdapter {
   public void windowClosing( WindowEvent e )
   {
      System.exit( 0 );
   }
}


public class Client extends Frame {
   private TextArea display;

   public Client()
   {
      super( "Client" );
      display = new TextArea( "", 0, 0,
                    TextArea.SCROLLBARS_VERTICAL_ONLY );
      add( display, BorderLayout.CENTER );
      setSize( 300, 150 );
      setVisible( true );
   }

   public void runClient()
   {
      Socket client;
      DataInputStream input;
      DataOutputStream output;

      try {
         // Step 1: Create a Socket to make connection.
         client = new Socket( InetAddress.getLocalHost(),
                              5000 );

         display.append( "Connected to: " +
            client.getInetAddress().getHostName() );

         // Step 2: Get the input and output streams.
         input = new DataInputStream(
                     client.getInputStream() );
         output = new DataOutputStream(
                      client.getOutputStream() );
         display.append( "\nGot I/O Streams\n" );

         // Step 3: Process connection.
         display.append( "Server message: " +
            input.readUTF() );
         display.append(
            "\nSending message \"Thank you.\"\n" );
         output.writeUTF( "Thank you." );

         // Step 4: Close connection.
         display.append( "Transmission complete. " +
            "Closing connection.\n" );
         client.close();
      }
      catch ( IOException e ) {
         e.printStackTrace();
      }
   }

   public static void main( String args[] )
   {
      Client c = new Client();

      c.addWindowListener( new CloseWindowAndExit() );
      c.runClient();
   }
}
 
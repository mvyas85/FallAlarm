/*
package com.exercise.AndroidClient;

import android.app.Activity;
import android.os.Bundle;

public class AndroidClient extends Activity {
    /** Called when the activity is first created. */
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
*/

package com.exercise.AndroidClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidClient extends Activity 
{

	EditText textOut;
	TextView textIn;

 	/** Called when the activity is first created. */
	 @SuppressLint("NewApi")
	@Override
	 public void onCreate(Bundle savedInstanceState) 
	 {
	     
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.main);
	  
	     textOut = (EditText)findViewById(R.id.textout);
	     Button buttonSend = (Button)findViewById(R.id.send);
	     textIn = (TextView)findViewById(R.id.textin);
	     buttonSend.setOnClickListener(buttonSendOnClickListener);
	 }

	 Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener()
	 {

		public void onClick(View arg0) 
		{
			 Socket socket = null;
			 DataOutputStream dataOutputStream = null;
			 DataInputStream dataInputStream = null;
		
			 try 
			 {
				  socket = new Socket("192.168.1.104", 5000);
				  dataOutputStream = new DataOutputStream(socket.getOutputStream());
				  dataInputStream = new DataInputStream(socket.getInputStream());
				  dataOutputStream.writeUTF(textOut.getText().toString());
				  textIn.setText(dataInputStream.readUTF());
			 } 
			 catch (UnknownHostException e) 
			 {
				 e.printStackTrace();
			 } 
			 catch (IOException e) 
			 {
				 e.printStackTrace();
			 }
			 finally
			 {
					if (socket != null)
					{
						 try 
						 {
							 socket.close();
						 } 
						 catch (IOException e) 
						 {
							 e.printStackTrace();
						 }
					}
					if (dataOutputStream != null)
					{
						   try 
						   {
						    dataOutputStream.close();
						   } 
						   catch (IOException e) 
						   {
							   e.printStackTrace();
						   }
					}
					if (dataInputStream != null)
					{
						  try 
						  {
							  dataInputStream.close();
						  } 
						  catch (IOException e) 
						  {
							  e.printStackTrace();
						  }
					}
			 	}
			}
	 	};
}
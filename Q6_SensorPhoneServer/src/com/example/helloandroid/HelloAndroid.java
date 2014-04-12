package com.example.helloandroid;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelloAndroid extends Activity implements SensorEventListener,Runnable {
	private SensorManager sensorManager;

	TextView x1; // declare X axis object
	TextView y1; // declare Y axis object
	TextView z1; // declare Z axis object
	
	TextView x2; // declare X axis object
	TextView y2; // declare Y axis object
	TextView z2; // declare Z axis object
	
	String x1Str,y1Str,z1Str,x2Str,y2Str,z2Str ;
	String oldX1,oldY1,oldZ1,oldX2,oldY2,oldZ2;

	Button sendAtATime,startContinous,dataChanged;
	private boolean startStop = false ,valueChanged = true;
	
	public HelloAndroid(){}
	
	public HelloAndroid(String x1Str, String y1Str, String z1Str, String x2Str,
			String y2Str, String z2Str) {
		super();
		this.x1Str = x1Str;
		this.y1Str = y1Str;
		this.z1Str = z1Str;
		this.x2Str = x2Str;
		this.y2Str = y2Str;
		this.z2Str = z2Str;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){

		 StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	        .detectDiskReads()
	        .detectDiskWrites()
	        .detectNetwork()   // or .detectAll() for all detectable problems
	        .penaltyLog()
	        .build());
	      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	        .detectLeakedSqlLiteObjects()
	        .detectLeakedClosableObjects()
	        .penaltyLog()
	        .penaltyDeath()
	        .build()); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		x1=(TextView)findViewById(R.id.x1); // create X axis object
		y1=(TextView)findViewById(R.id.y1); // create Y axis object
		z1=(TextView)findViewById(R.id.z1); // create Z axis object
		
		x2=(TextView)findViewById(R.id.x2); // create X axis object
		y2=(TextView)findViewById(R.id.y2); // create Y axis object
		z2=(TextView)findViewById(R.id.z2); // create Z axis object
		
		sendAtATime =  (Button)findViewById(R.id.sendAtATime);
		startContinous =  (Button)findViewById(R.id.startContinuous);
			     
	    sendAtATime.setOnClickListener(buttonSendOnClickListener);
	    startContinous.setOnClickListener(buttonContinuousClickListener);
	    
	    sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		// add listener. The listener will be HelloAndroid (this) class
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void onAccuracyChanged(Sensor sensor,int accuracy){
	}

	public void onSensorChanged(SensorEvent event)
	{
		// check sensor type
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
		{
			oldX1 = x1.getText().toString();
			oldY1 = y1.getText().toString();
			oldZ1 = z1.getText().toString();
			
			// assign directions/
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			x1.setText("X1: "+x);
			y1.setText("Y1: "+y);
			z1.setText("Z1: "+z);
			
		}
		if(event.sensor.getType()==Sensor.TYPE_ORIENTATION)
		{
			oldX2 = x2.getText().toString();
			oldY2 = y2.getText().toString();
			oldZ2 = z2.getText().toString();
			
			// assign directions/
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			x2.setText("X2: "+x);
			y2.setText("Y2: "+y);
			z2.setText("Z2: "+z);
		}
		
		if(x1.getText().toString().equals(oldX1) && y1.getText().toString().equals(oldY1) 
		&& z1.getText().toString().equals(oldZ1) && x2.getText().toString().equals(oldX2)
		&& y2.getText().toString().equals(oldY2) && z2.getText().toString().equals(oldZ2) )
		{
			valueChanged = false;
		}
		else
		{
			valueChanged = true;
		}
		if(startStop && valueChanged)
		{
			
			Thread aThread = new Thread(new HelloAndroid(x1.getText().toString()
														,y1.getText().toString()
														,z1.getText().toString()
														,x2.getText().toString()
														,y2.getText().toString()
														,z2.getText().toString()));
			aThread.run();
		}
	}
	
	Button.OnClickListener buttonContinuousClickListener = new Button.OnClickListener()
	{
		public void onClick(View arg0) 
		{
			if(startStop)
			{
				startStop = false;
				startContinous.setText("Start Sending Continous");
				return;
			}
			startStop = true;
			startContinous.setText("Stop Sending Continous");
		}
	 };
	 Button.OnClickListener buttonSendOnClickListener = new Button.OnClickListener()
	 {
		public void onClick(View arg0) 
		{
			Thread aThread = new Thread(new HelloAndroid(x1.getText().toString()
														,y1.getText().toString()
														,z1.getText().toString()
														,x2.getText().toString()
														,y2.getText().toString()
														,z2.getText().toString()));
			aThread.run();
		}
	 };
	public void run() 
	{
		Socket socket = null;
		 DataOutputStream dataOutputStream = null;
		 DataInputStream dataInputStream = null;
	
		 try 
		 {
			  socket = new Socket("192.168.1.104", 5000);
			  dataOutputStream = new DataOutputStream(socket.getOutputStream());
			  dataInputStream = new DataInputStream(socket.getInputStream());
			  dataOutputStream.writeUTF("\nAcceleration Values :\n"
					  					+x1Str+"\n"
					  					+y1Str+"\n"
					  					+z1Str+"\n"
					  					+"Orientation Values :\n"
					  					+x2Str+"\n"
					  					+y2Str+"\n"
					  					+z2Str+"\n");
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
}
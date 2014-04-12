/*
 * Port of OpenIntents simulator to Android 2.1, extension to multi
 * emulator support, and GPS and battery simulation is developed as a
 * diploma thesis of Josip Balic at the University of Zagreb, Faculty of
 * Electrical Engineering and Computing.
 * 
 * Copyright (C) 2008-2010 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.sensorsimulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.openintents.sensorsimulator.db.SensorSimulator;
import org.openintents.sensorsimulator.db.SensorSimulatorConvenience;
import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorEventListener;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
import org.openintents.sensorsimulator.hardware.SensorNames;
import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SensorSimulatorSettingsActivity extends Activity implements Runnable{

	private static final String TAG = "SensorSimulatorSettingsActivity";

    private SensorManagerSimulator mSensorManager;
    
	
	// Indicators whether real device or sensor simulator is connected.
	private TextView mTextSensorType;
	
	private Button mButtonConnect;
	private Button mButtonDisconnect;
	Button sendAtATime,startContinous,dataChanged;
	private LinearLayout mSensorsList;
	private boolean startStop = false ;
	DecimalFormat mDecimalFormat;
	
	ArrayList<String> mSupportedSensors = null;// = new ArrayList<String>();

	int mNumSensors;
	String Sdata=null;
	boolean[] mSensorEnabled;

	SingleSensorView[] mSingleSensorView;
	String[] sensorData ;
	
	String[] mDelayTypes = new String[] { "Fastest", "Game", "UI", "Normal" };
	int[] mDelayValue = new int[] {
			SensorManager.SENSOR_DELAY_FASTEST,
			SensorManager.SENSOR_DELAY_GAME, 
			SensorManager.SENSOR_DELAY_UI, 
			SensorManager.SENSOR_DELAY_NORMAL};
	
	private SensorSimulatorConvenience mSensorSimulatorConvenience;
    
	
	public  SensorSimulatorSettingsActivity(){}
	public  SensorSimulatorSettingsActivity (String data)
	{
		this.Sdata=data;
	}
	public  SensorSimulatorSettingsActivity (String[] data, int mNumSensors)
	{
		this.mNumSensors = mNumSensors;
		this.sensorData = data;
	}
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	
		//sensorData=null;
		setContentView(R.layout.sensorsimulator);

		//add as sensor manager our's sensor manager simulator
		mSensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
		
		//set convenience for storing and loading IP and port for connection
		mSensorSimulatorConvenience = new SensorSimulatorConvenience(this);
			
		mButtonConnect = (Button) findViewById(R.id.buttonconnect);
		
		sendAtATime =  (Button)findViewById(R.id.sendAtATime);
		startContinous =  (Button)findViewById(R.id.startContinuous);
		mSupportedSensors = new ArrayList<String>();	     
	    sendAtATime.setOnClickListener(buttonSendOnClickListener);
	    startContinous.setOnClickListener(buttonContinuousClickListener);
		mButtonConnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				connect();
			}
		});

		mButtonDisconnect = (Button) findViewById(R.id.buttondisconnect);
		mButtonDisconnect.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				disconnect();
			}
		});
		
		setButtonState();

		mTextSensorType = (TextView) findViewById(R.id.datatype);
		
		// Format for output of data
		mDecimalFormat = new DecimalFormat("#0.00");
		      
		readAllSensors(); // Basic sensor information
		
		mSensorsList = (LinearLayout) findViewById(R.id.sensordatalist);

		fillSensorList(); // Fills the sensor list manually, giving us more control
		
		//if we use simulation of GPS, here we register location manager for the GPS
		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = new MyLocationListener();
		mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
		
	}
	public class MyLocationListener implements LocationListener{

		/**
		 * Method that gets location changes. Once location is received, Toast message
		 * with Latitude, Longitude and Altitude is made. This is just an example how
		 * LocationListener should be used in applications.
		 */
		@Override
		public void onLocationChanged(Location location) {
			location.getLatitude();
			location.getLongitude();
			location.getAltitude();

			String Text = "My current location is: " + "Longitude = " + location.getLongitude() + 
			" Latitude = " + location.getLatitude() ;//+ " Altitude = " + location.getAltitude();
			
			Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
	}
	
	/**
	 * Called when activity comes to foreground. In onResume() method
	 * we register our sensors listeners, important - sensor listeners
	 * are not and should not be registered before, but in on resume
	 * method.
	 */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener, mSensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE), 
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener,
        		mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
        		SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(listener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_FASTEST);    
    }

    /**
     * Called when activity is stopped. Here we unregister all of currently
     * existing listeners.
     */
    @Override
    protected void onStop() {
    	mSensorManager.unregisterListener(listener);
        super.onStop();
    }
    
    /**
     * Called when another activity is started.
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState (outState);
    }

	/**
	 * Called when the user leaves.
	 * Here we store the IP address and port, unregister existing listeners and
	 * close the connection with sensor simulator.
	 */
    @Override
    protected void onPause() {
        super.onPause();

		//mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_IPADDRESS);
		//mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_SOCKET);
	}
	
    ////////////////////////////////////////////////////////
    // The menu

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
				
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		}
		return super.onOptionsItemSelected(item);		
	}

	///////////////////////////////////////
	// Menu related functions

	///////////////////////////////////////
	
	/**
	 * This method is used to connect our activity with sensor simulator.
	 * If we already have opened connection, than we unregister all the listeners,
	 * close existing connection and we create new connection.
	 */
	public void connect() {
		Log.i(TAG, "Connect");
		String newIP = "10.0.2.2"; //mEditTextIP.getText().toString();
		String newSocket ="8010"; // mEditTextSocket.getText().toString();
		String oldIP = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_IPADDRESS);
		String oldSocket = mSensorSimulatorConvenience.getPreference(SensorSimulator.KEY_SOCKET);
		
		if (! (newIP.contentEquals(oldIP) && newSocket.contentEquals(oldSocket)) ) {
			// new values
			mSensorManager.unregisterListener(listener);
			mSensorManager.disconnectSimulator();
			
			// Save the values
			mSensorSimulatorConvenience.setPreference(SensorSimulator.KEY_IPADDRESS, newIP);
			mSensorSimulatorConvenience.setPreference(SensorSimulator.KEY_SOCKET, newSocket);
		}
		
		if (! mSensorManager.isConnectedSimulator() ) {
			Log.i(TAG, "Not connected yet -> Connect");
			mSensorManager.connectSimulator();
		}

		readAllSensors();
		
		setButtonState();
		
 		if (mSensorManager.isConnectedSimulator()) {
			mTextSensorType.setText(R.string.sensor_simulator_data);
		} else {
			mTextSensorType.setText(R.string.real_device_data);
		}
 		
 		fillSensorList();		
	}
	
	/**
	 * Method disconnect() is used to close existing connection with sensor
	 * simulator. When disconnect is called, all registered sensors are getting
	 * unregistered.
	 */
	public void disconnect() {
		mSensorManager.unregisterListener(listener);
		mSensorManager.disconnectSimulator();
		
		readAllSensors();
		
		setButtonState();
		
 		if (mSensorManager.isConnectedSimulator()) {
			mTextSensorType.setText(R.string.sensor_simulator_data);
		} else {
			mTextSensorType.setText(R.string.real_device_data);
		}

 		fillSensorList();
	}
	
	/**
	 * This method is used to set button states. If we are connected with sensor
	 * simulator, than we can't click on button connect anymore and vice versa.
	 */
	public void setButtonState() {
		boolean connected = mSensorManager.isConnectedSimulator();
		mButtonConnect.setEnabled(!connected);
		
		mButtonDisconnect.setEnabled(connected);
		
		mButtonConnect.invalidate();
		mButtonDisconnect.invalidate();
	}
    
    /**
     * Reads information about which sensors are supported and what their rates and current rates are.
     */
	public void readAllSensors() {
		
        Log.d(TAG, "Sensors: " + mSensorManager.getSensors());
        Log.d(TAG, "Connected: " + mSensorManager.isConnectedSimulator());
        
        ArrayList<Integer> sensors = mSensorManager.getSensors();
        
        if(sensors!=null){
        mSupportedSensors = SensorNames.getSensorNames(sensors);
        }
        
		// Now set values that are related to sensor updates:
        if(mSupportedSensors!=null){
        mNumSensors = mSupportedSensors.size();
        sensorData = new String[mNumSensors];
		
        }       
	}
	
	/**
	 * Our listener for this application. This listener holds all sensors 
	 * we enable. If we are developing application which is using 2 or more
	 * sensors, it's advisable to use only one listener per one sensor.
	 */
	private SensorEventListener listener = new SensorEventListener(){
		

		/**
		 * onAccuracyChanged must be added, but it doesn't need any editing.
		 */
		@Override
		public void onAccuracyChanged(Sensor sensor, int acc){
		}
		@Override
		public void onSensorChanged(SensorEvent event){
			int sensor = event.type;
			float[] values = event.values;
			
			//sensorData.subList(x,sensorData.size()).clear();
			for (int i = 0; i < mNumSensors; i++) {
	        	if ((mSingleSensorView[i].mSensorBit == sensor) && sensor!=9) {
	        		// Update this view
	        		String data = "";
	        		int num = SensorNames.getNumSensorValues(sensor);
	        		
	        		for (int j = 0; j < num; j++) {
	        			data += mDecimalFormat.format(values[j]);
	        			if (j < num-1) data += ", ";
	        			
	        		}
	                	
	        		mSingleSensorView[i].mTextView.setText(data);
	        		Sdata = data;
	        		sensorData[i] = data;
	        	}
			}
			
			//If Barcode is enabled, this method is used only for Barcode output
			for (int i = 0; i < mNumSensors; i++) {
	        	if ((mSingleSensorView[i].mSensorBit == sensor) && sensor==9) {
	        		// Update this view
	        		String data = "";
                    data = event.barcode;
	                	
	        		mSingleSensorView[i].mTextView.setText(data);
	        		break;
	        	}
			}
			
			if(startStop)
			{
				Thread aThread = new Thread(new  SensorSimulatorSettingsActivity(sensorData,mNumSensors));
				aThread.run();
			}
		}

	};

	/** 
     * Fills the sensor list with currently active sensors.
     */
	
    void fillSensorList() {
    	if(mSupportedSensors!=null){
        mSensorsList.removeAllViews();
    	
    	// Now we fill the list, one by one:
    	int max = mSupportedSensors.size();

    	mSingleSensorView = new SingleSensorView[max];
    	
    	for (int i=0; i < max; i++) {
    		String[] sensorsNames = new String[mSupportedSensors.size()];
    		ArrayList<Integer> sensorbit = SensorNames.getSensorsFromNames(mSupportedSensors.toArray(sensorsNames));
    		SingleSensorView ssv = 
    			new SingleSensorView(this, 
    					mSupportedSensors.get(i), 
    					sensorbit.get(i),
    					i);
    		ssv.setLayoutParams(new LinearLayout.LayoutParams(
    				LinearLayout.LayoutParams.FILL_PARENT,
    				LinearLayout.LayoutParams.WRAP_CONTENT));
    		mSensorsList.addView(ssv, i);
    		mSingleSensorView[i] = ssv;
    	}
    	}
    }
    	
    /**
     * Layout for displaying single sensor.
     * 
     * @author Peli
     * @author Josip Balic
     */
    private class SingleSensorView extends LinearLayout {

        @SuppressWarnings("unused")
		private TextView mTitle;
        
        @SuppressWarnings("unused")
		LinearLayout mL1;
        @SuppressWarnings("unused")
		LinearLayout mL1a;
        @SuppressWarnings("unused")
		LinearLayout mL1b;
        @SuppressWarnings("unused")
		LinearLayout mL1c;
        
        TextView  mCheckBox;
        TextView mTextView;
        
        @SuppressWarnings("unused")
		Context mContext;
        
        @SuppressWarnings("unused")
		int mSensorId;
        String mSensor;
        int mSensorBit;
        
        
        @SuppressWarnings("unused")
		int mDefaultValueIndex;
		
    	public SingleSensorView(Context context, String sensor, int sensorbit, int sensorId) {
    		super(context);
    		Log.i(TAG, "SingleSensorView - constructor");
    		
    		mContext = context;
    		mSensorId = sensorId;
    		mSensor = sensor;
    		mSensorBit = sensorbit;
    		System.out.println(mSensorBit);
    		mDefaultValueIndex = -1;  // -1 means there is no default index.
    		
    		// Build child view from resource:
    		LayoutInflater inf = 
    			(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE); 
    		View rowView = inf.inflate(R.layout.sensorsimulator_row, null); 
    		
    		// We set a tag, so that Handler can find this view
    		rowView.setTag(mSensor);
    		
    		// Assign widgets
    		mCheckBox = (TextView) rowView.findViewById(R.id.enabled);
    		mCheckBox.setText(sensor);
    		
    		mTextView = (TextView) rowView.findViewById(R.id.sensordata);
    		mTextView.setText(sensor);
            
    		addView(rowView, new LinearLayout.LayoutParams(
        			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		
    		mSensorManager.registerListener(listener,mSensorManager.getDefaultSensor(mSensorBit),
					SensorManager.SENSOR_DELAY_NORMAL); 
    		
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
			Thread aThread = new Thread(new  SensorSimulatorSettingsActivity(sensorData,mNumSensors));
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
			  String sendToServer="";
			  for(int i=0;i<sensorData.length;i++)
			  {
				 sendToServer=sendToServer+"\n"+sensorData[i];
			  }
			  dataOutputStream.writeUTF(sendToServer);
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

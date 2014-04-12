package com.example.helloandroid;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class HelloAndroid extends Activity implements SensorEventListener {
	private SensorManager sensorManager;

	TextView xCoor1; // declare X axis object
	TextView yCoor1; // declare Y axis object
	TextView zCoor1; // declare Z axis object
	
	TextView xCoor2; // declare X axis object
	TextView yCoor2; // declare Y axis object
	TextView zCoor2; // declare Z axis object

	@Override
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		xCoor1=(TextView)findViewById(R.id.xcoor1); // create X axis object
		yCoor1=(TextView)findViewById(R.id.ycoor1); // create Y axis object
		zCoor1=(TextView)findViewById(R.id.zcoor1); // create Z axis object
		
		xCoor2=(TextView)findViewById(R.id.xcoor2); // create X axis object
		yCoor2=(TextView)findViewById(R.id.ycoor2); // create Y axis object
		zCoor2=(TextView)findViewById(R.id.zcoor2); // create Z axis object

		sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
		// add listener. The listener will be HelloAndroid (this) class
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_NORMAL);
		
		/*	More sensor speeds (taken from api docs)
		    SENSOR_DELAY_FASTEST get sensor data as fast as possible
		    SENSOR_DELAY_GAME	rate suitable for games
		 	SENSOR_DELAY_NORMAL	rate (default) suitable for screen orientation changes
		*/
	}

	public void onAccuracyChanged(Sensor sensor,int accuracy){

	}

	public void onSensorChanged(SensorEvent event){

		// check sensor type
		if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){

			// assign directions/
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			xCoor1.setText("X1: "+x);
			yCoor1.setText("Y1: "+y);
			zCoor1.setText("Z1: "+z);
		}
		
		if(event.sensor.getType()==Sensor.TYPE_ORIENTATION){

			// assign directions/
			float x=event.values[0];
			float y=event.values[1];
			float z=event.values[2];

			xCoor2.setText("X2: "+x);
			yCoor2.setText("Y2: "+y);
			zCoor2.setText("Z2: "+z);
		}
	}
}

/*
package com.example.helloandroid;

import android.app.Activity;
import android.os.Bundle;

public class HelloAndroid extends Activity {
*/
/*
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
*/
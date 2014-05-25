package com.example.activityrecognitiontest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.TextureView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {

	ActivityRecognitionClient recognitionClient;
	ActivityReceiver receiver;
	SharedPreferences preferences;
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preferences = getSharedPreferences("thisapp", 0);
		
		textView = (TextView) findViewById(R.id.tvActivity);
		
		

		recognitionClient = new ActivityRecognitionClient(this, this, this);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		recognitionClient.connect();

		IntentFilter filter = new IntentFilter(ActivityReceiver.Action);
		receiver = new ActivityReceiver();
		registerReceiver(receiver, filter);
	}

	@Override
	protected void onStop() {
		unregisterReceiver(receiver);
		super.onStop();
	}

	private void updateUi(int type) {
		String text = textView.getText().toString();
		text += "\n" + getActivityName(type);
		textView.setText(text);
	}
	
	private String getActivityName(int type) {
		String activityName = null;
		
		switch (type) {
		case DetectedActivity.STILL:
			activityName = "still";
			break;
		case DetectedActivity.TILTING:
			activityName = "Tilting";
			break;
		case DetectedActivity.ON_FOOT:
			activityName = "on foot";
			break;
		case DetectedActivity.ON_BICYCLE:
			activityName = "on bycicle";
			break;
		case DetectedActivity.IN_VEHICLE:
			activityName = "in vehicle";
			break;
		case DetectedActivity.UNKNOWN:
			activityName = "unknown";
			break;
		}
		
		return activityName;
	}

	public class ActivityReceiver extends BroadcastReceiver {
		public static final String Action = "com.example.activityrecognitiontest.RECIEVE";

		@Override
		public void onReceive(Context contex, Intent intent) {
			if (ActivityRecognitionResult.hasResult(intent)) {
				ActivityRecognitionResult result = ActivityRecognitionResult
						.extractResult(intent);
				DetectedActivity detectedActivity = result.getMostProbableActivity();
				int type = detectedActivity.getType();
				updateUi(type);
			}
		}

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		Intent intent = new Intent(ActivityReceiver.Action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		recognitionClient.requestActivityUpdates(5000, pendingIntent);
		recognitionClient.disconnect();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

}

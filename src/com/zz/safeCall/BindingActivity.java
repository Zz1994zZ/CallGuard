package com.zz.safeCall;

import com.emobile.localservicedemo.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class BindingActivity extends Activity {
	private boolean mIsBound;

	private LocalService mBoundService;

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((LocalService.LocalBinder) service).getService();
			Toast.makeText(BindingActivity.this,
					R.string.local_service_connected, Toast.LENGTH_SHORT)
					.show();
		}
		@Override
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
			Toast.makeText(BindingActivity.this,
					R.string.local_service_disconnected, Toast.LENGTH_SHORT)
					.show();
		}
	};


	void doBindService() {
		bindService(new Intent(BindingActivity.this, LocalService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
	}

	private OnClickListener mBindListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			doBindService();
		}
	};

	private OnClickListener mUnbindListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			doUnbindService();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.local_service_binding);

		// Watch for button clicks.
		Button button = (Button) findViewById(R.id.bind);
		button.setOnClickListener(mBindListener);
		button = (Button) findViewById(R.id.unbind);
		button.setOnClickListener(mUnbindListener);
	}
}
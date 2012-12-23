package be.fbousson.voicecontroller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SoundRecorderActivity extends Activity {

	private static final String TAG = SoundRecorderActivity.class.getName();

	private Button recordStartSoundButton;
	private Button playStartSoundButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.soundrecorder);
		initControls();
		initBehaviour();
	}

	private void initBehaviour() {
		recordStartSoundButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.d(TAG, "RecordButton clicked");

			}
		});

		playStartSoundButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.d(TAG, "playStartSoundButton clicked");

			}
		});

	}

	private void initControls() {
		recordStartSoundButton = (Button) findViewById(R.id.recordStartSoundButton);
		playStartSoundButton = (Button) findViewById(R.id.playStartSoundButton);

	}

}

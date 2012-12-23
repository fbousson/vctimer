package be.fbousson.voicecontroller;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class HelloAndroidActivity extends Activity {

	private static String TAG = "vctimer";

	private Chronometer chronometerView;

	private Button startButton;
	private Button stopButton;
	private Button resetButton;

	private TextView countdownTimerField;

	private CountDownTimer countDownTimer;
	private SeekBar seekBar;

	private Handler mHandler = new Handler();
	private ProgressBar progressBar;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.main);

		initControls();

		chronometerView.setOnChronometerTickListener(new OnChronometerTickListener() {

			public void onChronometerTick(Chronometer chronometer) {
				long elapsedMillis = SystemClock.elapsedRealtime() - chronometerView.getBase();
				Log.d(TAG, "chronometer tick " + elapsedMillis);

			}
		});

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int seconds = seekBar.getProgress();
				startTimer(seconds);
				stopViewState();
			}

		});

		stopButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (countDownTimer != null) {
					countDownTimer.cancel();
				}
				startViewState();

			}

		});
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {

			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				countdownTimerField.setText(String.valueOf(progress));
			}
		});

	}

	private void initControls() {
		chronometerView = (Chronometer) findViewById(R.id.chronometer);
		startButton = (Button) findViewById(R.id.startTimerButton);
		stopButton = (Button) findViewById(R.id.stopTimerButton);
		resetButton = (Button) findViewById(R.id.resetTimerButton);
		countdownTimerField = (TextView) findViewById(R.id.countdownTimerField);
		seekBar = (SeekBar) findViewById(R.id.timerbar);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

	}

	public void startTimer(int seconds) {
		progressBar.setMax(seconds);

		countDownTimer = new CountDownTimer(seconds * 1000, 1000) {

			public void onTick(long millisUntilFinished) {
				final int secondsLeft = (int) (millisUntilFinished / 1000);
				countdownTimerField.setText("seconds remaining: " + secondsLeft);
				mHandler.post(new Runnable() {
					public void run() {
						progressBar.setProgress(secondsLeft);
					}
				});

			}

			public void onFinish() {
				countdownTimerField.setText("done!");
				startViewState();
			}
		}.start();

	}

	private void stopViewState() {
		progressBar.setVisibility(View.VISIBLE);
		stopButton.setVisibility(View.VISIBLE);
		startButton.setVisibility(View.GONE);
		countdownTimerField.setVisibility(View.GONE);
		seekBar.setVisibility(View.GONE);
	}

	private void startViewState() {
		seekBar.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		startButton.setVisibility(View.VISIBLE);
		stopButton.setVisibility(View.GONE);
		countdownTimerField.setVisibility(View.VISIBLE);
	}

}

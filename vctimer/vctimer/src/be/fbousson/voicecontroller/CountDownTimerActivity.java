package be.fbousson.voicecontroller;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import be.fbousson.voicecontroller.musicg.DetectorThread;
import be.fbousson.voicecontroller.musicg.RecorderThread;

public class CountDownTimerActivity extends Activity {

	private static String TAG = CountDownTimerActivity.class.getName();

	private ToggleButton voiceControlToggleButton;
	private Chronometer chronometerView;

	private Button startButton;
	private Button stopButton;
	private Button resetButton;

	private TextView countdownTimerField;

	private static CountDownTimer countDownTimer;
	private SeekBar seekBar;

	private volatile Handler mHandler = new Handler();
	private ProgressBar progressBar;
	private TextView progressTextView;

	private static final String DETECTOR_THREAD = "DETECTOR_THREAD";
	private static final String RECORDER_THREAD = "RECORDER_THREAD";
	private static final String DETECTED_TEXT_THREAD = "DETECTED_TEXT_THEAD";

	private int maxSeconds;
	private static final String MAX_SECONDS = "maxSeconds";

	// TODO fbousson fix threads dissapearing on rotate
	// detection parameters
	private static DetectorThread detectorThread;
	private static RecorderThread recorderThread;
	private static Thread detectedTextThread;

	private static final int INITIAL_COUNTDOWN_TIMER = 5;

	private Vibrator vibrator;

	private Button goToSoundRecorder;

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

		// getLastNonConfigurationInstance().
		// getLastNonConfigurationInstance();
		// mHandler = new Handler();

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		initControls();

		voiceControlToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "voiceControlToggleButton checked : " + isChecked);
				if (isChecked) {
					startDetectorThreads();
				} else {
					stopDectectorThreads();
				}

			}

		});

		// chronometerView.setOnChronometerTickListener(new
		// OnChronometerTickListener() {
		//
		// public void onChronometerTick(Chronometer chronometer) {
		// long elapsedMillis = SystemClock.elapsedRealtime() -
		// chronometerView.getBase();
		// Log.d(TAG, "chronometer tick " + elapsedMillis);
		//
		// }
		// });

		goToSoundRecorder.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				launchSoundRecorder();
			}
		});

		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startCountDown();
			}

		});

		stopButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				stopCountDown();

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

		if (countDownTimer == null) {
			countdownTimerField.setText(String.valueOf(INITIAL_COUNTDOWN_TIMER));
			seekBar.incrementProgressBy(INITIAL_COUNTDOWN_TIMER);
			startViewState();
		} else {
			stopViewState();
		}

	}

	public void launchSoundRecorder() {
		Intent intent = new Intent(this, SoundRecorderActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		stopCountDown();
		stopDectectorThreads();
	}

	private void initControls() {
		chronometerView = (Chronometer) findViewById(R.id.chronometer);
		startButton = (Button) findViewById(R.id.startTimerButton);
		stopButton = (Button) findViewById(R.id.stopTimerButton);
		resetButton = (Button) findViewById(R.id.resetTimerButton);
		countdownTimerField = (TextView) findViewById(R.id.countdownTimerField);
		seekBar = (SeekBar) findViewById(R.id.timerbar);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressTextView = (TextView) findViewById(R.id.progressTextView);
		voiceControlToggleButton = (ToggleButton) findViewById(R.id.voiceControlToggleButton);
		goToSoundRecorder = (Button) findViewById(R.id.goToSoundRecorderButton);

	}

	private void startTimer(int seconds) {
		progressBar.setMax(seconds);

		countDownTimer = new CountDownTimer(seconds * 1000, 1000) {

			public void onTick(long millisUntilFinished) {
				final int secondsLeft = (int) (millisUntilFinished / 1000);
				countdownTimerField.setText("seconds remaining: " + secondsLeft);
				mHandler.post(new Runnable() {
					public void run() {
						progressBar.setProgress(secondsLeft);
						progressTextView.setText("seconds remaining: " + secondsLeft);
					}
				});

			}

			public void onFinish() {
				// if(vibrabot.hasVibrator()){
				vibrator.vibrate(1000);
				// }
				countdownTimerField.setText("done!");
				unassignCounter();
				startViewState();
			}
		}.start();

	}

	private void startCountDown() {
		Log.d(TAG, "Starting countdown");
		int maxSeconds = seekBar.getProgress();
		startTimer(maxSeconds);
		stopViewState();
	}

	private void stopCountDown() {
		if (countDownTimer != null) {
			countDownTimer.cancel();
		}

		unassignCounter();
		startViewState();
	}

	public void unassignCounter() {
		if (detectorThread != null) {
			detectorThread.resetTotalWhistles();
		}
		countDownTimer = null;
	}

	private void stopViewState() {
		progressBar.setVisibility(View.VISIBLE);
		progressTextView.setVisibility(View.VISIBLE);
		stopButton.setVisibility(View.VISIBLE);
		startButton.setVisibility(View.GONE);
		countdownTimerField.setVisibility(View.GONE);
		seekBar.setVisibility(View.GONE);

	}

	private void startViewState() {
		seekBar.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		progressTextView.setVisibility(View.GONE);
		startButton.setVisibility(View.VISIBLE);
		stopButton.setVisibility(View.GONE);
		countdownTimerField.setVisibility(View.VISIBLE);

	}

	

	// TODO refactor this to a service.

	private void stopDectectorThreads() {
		if (recorderThread != null) {
			recorderThread.stopRecording();
			recorderThread = null;
		}
		if (detectorThread != null) {
			detectorThread.stopDetection();
			detectorThread = null;
		}

	}

	private void startDetectorThreads() {
		if (recorderThread == null && detectorThread == null) {
			recorderThread = new RecorderThread();
			recorderThread.start();
			detectorThread = new DetectorThread(recorderThread);
			detectorThread.start();

			// TODO pass handler to detectorthread?
			if (detectedTextThread == null) {
				detectedTextThread = new Thread() {
					public void run() {
						try {
							while (recorderThread != null && detectorThread != null) {
								if (allowStartOfCountDownTimer()) {
									runOnUiThread(new Runnable() {
										public void run() {
											startCountDown();
										}
									});
								}

								sleep(100);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							detectedTextThread = null;
						}
					}

					private boolean allowStartOfCountDownTimer() {
						boolean countDownTimerIsNull = countDownTimer == null;
						boolean hasWhistles = detectorThread.getTotalWhistlesDetected() > 0;
						boolean allow = countDownTimerIsNull && hasWhistles;
						if (allow) {
							Log.d(TAG, "allowing creation of timer");
						}
						return allow;
					}
				};
				detectedTextThread.start();
			}

		}

	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		// task.detach(); return(task);
		Map<String, Object> state = new HashMap<String, Object>();

		return state;
	}

	//

}

package kr.bae.autocallrecoder.recorder;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarMonitor implements OnSeekBarChangeListener {

	private SeekBar seekBar;
	private TextView tv;
	private MediaPlayer mediaPlayer;
	private ScheduledFuture scheduledFuture;
	SimpleDateFormat format = new SimpleDateFormat("mm:ss");
	private boolean isStop;
	private int firstDuration;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			monitor();
		}
	};

	public SeekBarMonitor(SeekBar seekBar, MediaPlayer mediaPlayer, TextView tv) {
		this.seekBar = seekBar;
		this.mediaPlayer = mediaPlayer;
		this.firstDuration = mediaPlayer.getDuration();
		this.tv = tv;
		init();
	}

	public void setCurrentStopMode(boolean isStop) {
		this.isStop = isStop;
	}

	private void init() {
		seekBar.setProgress(0);	
		seekTo(0);
		tv.setText("00:00");
		
		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

		scheduledFuture = service.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {

				handler.sendMessage(handler.obtainMessage(0));
			}
		}, 200, 200, TimeUnit.MILLISECONDS);

		seekBar.setOnSeekBarChangeListener(this);
	}

	private void monitor() {
		try {
			if (isStop == true) {
				seekBar.setProgress(0);	
				seekTo(0);
				tv.setText(format.format(firstDuration));
			}
			
			if (mediaPlayer.isPlaying()) {
				int duration = mediaPlayer.getDuration();
				int position = mediaPlayer.getCurrentPosition();

				seekBar.setMax(duration);
				seekBar.setProgress(position);
				tv.setText(format.format(position));
			}
		} catch (Exception e) {

		}
	}

	public void cancel() {
		scheduledFuture.cancel(true);
		handler.removeMessages(0);
		tv.setText(format.format(firstDuration));
	}

	private void seekTo(int msec) {
		try {
			mediaPlayer.seekTo(msec);
		} catch (Exception e) {
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		if (arg2) {
			seekTo(progress);
			tv.setText(format.format(progress));
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}
}

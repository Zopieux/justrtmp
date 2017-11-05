package eu.zopi.android.justrtmp;

import android.media.AudioManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements IMediaPlayer.OnErrorListener,
    IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  private IjkMediaPlayer player;
  private TextView addressText;
  private Button playStopButton;

  private void preparePlayer() {
    player = new IjkMediaPlayer();
    player.setScreenOnWhilePlaying(true);
    player.setOnErrorListener(this);
    player.setOnPreparedListener(this);
    player.setOnInfoListener(this);
    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet_buffering", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-delay", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max_delay", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "tune", "zerolatency");
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reorder-queue-size", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reorder_queue_size", 0);

    player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip-frame", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip-loop-filter", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0);

    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "sync", "ext");

    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "vn", 1); // video null
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "nodisp", 1);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 32);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 0);
    player.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "formatprobesize", 0);
  }

  private void releasePlayer() {
    if (player == null) {
      return;
    }
    player.stop();
    player.release();
    player = null;
  }

  private boolean isPlaying() {
    return player != null && player.isPlaying();
  }

  private void showToast(final CharSequence msg) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        updateUi();
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    addressText = (TextView) findViewById(R.id.address);
    playStopButton = (Button) findViewById(R.id.play);

    addressText.setText(getString(R.string.default_address));

    playStopButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (isPlaying()) {
          releasePlayer();
          updateUi();
        } else {
          try {
            preparePlayer();
            player.setDataSource(addressText.getText().toString());
            player.prepareAsync();
            updateUi();
          } catch (final IOException e) {
            showToast(e.toString());
          }
        }
      }
    });
  }

  private void updateUi() {
    final boolean playing = player != null && player.isPlaying();
    playStopButton.setText(playing ? "Stop" : "Play");
  }

  @Override
  public boolean onError(IMediaPlayer iMediaPlayer, int err, int extra) {
    showToast(String.format("error: %d, %d (%s)", err, extra, player.getDataSource()));
    releasePlayer();
    updateUi();
    return true;
  }

  @Override
  public void onPrepared(IMediaPlayer iMediaPlayer) {
    Log.i(TAG, "onPrepared()");
    updateUi();
  }

  @Override
  public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int j) {
    Log.i(TAG, String.format("onInfo(%d, %d)", i, j));
    updateUi();
    return false;
  }
}

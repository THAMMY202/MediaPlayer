package com.player.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import androidx.core.content.ContextCompat
import com.chibde.visualizer.BarVisualizer


class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()
    private var pause: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Start the media player
        playBtn.setOnClickListener{
            try {
                if(pause){
                    mediaPlayer.seekTo(mediaPlayer.currentPosition)
                    mediaPlayer.start()
                    pause = false
                    barVisualization()
                    Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()
                }else{

                    mediaPlayer = MediaPlayer.create(applicationContext,R.raw.school_bell)
                    mediaPlayer.start()
                    barVisualization()
                    Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()
                }

                initializeSeekBar()
                playBtn.isEnabled = false
                pauseBtn.isEnabled = true
                stopBtn.isEnabled = true

                mediaPlayer.setOnCompletionListener {
                    playBtn.isEnabled = true
                    pauseBtn.isEnabled = false
                    stopBtn.isEnabled = false
                    Toast.makeText(this,"end",Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                Log.d(TAG,e.toString())
                showMsg(e.toString())
            }
        }

        // Pause the media player
        pauseBtn.setOnClickListener {
            try{
                if(mediaPlayer.isPlaying){
                    mediaPlayer.pause()
                    pause = true
                    playBtn.isEnabled = true
                    pauseBtn.isEnabled = false
                    stopBtn.isEnabled = true
                    Toast.makeText(this,"media pause",Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                Log.d(TAG,e.toString())
                showMsg(e.toString())
            }

        }
        // Stop the media player
        stopBtn.setOnClickListener{
            if(mediaPlayer.isPlaying || pause.equals(true)){
                pause = false
                seek_bar.setProgress(0)
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                tv_pass.text = ""
                tv_due.text = ""
                Toast.makeText(this,"media stop",Toast.LENGTH_SHORT).show()
            }
        }
        // Seek bar change listener
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }

    private fun initializeSeekBar() {
        seek_bar.max = mediaPlayer.seconds

        runnable = Runnable {
            seek_bar.progress = mediaPlayer.currentSeconds

            tv_pass.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tv_due.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }


    private val MediaPlayer.seconds: Int
        get() {
            return this.duration / 1000
        }

    private val MediaPlayer.currentSeconds: Int
        get() {
            return this.currentPosition / 1000
        }

    private fun barVisualization() {
        val barVisualizer = findViewById<BarVisualizer>(R.id.visualizerBar)
        barVisualizer.setColor(ContextCompat.getColor(this,R.color.myColor1))
        barVisualizer.setDensity(80f)
        barVisualizer.setPlayer(mediaPlayer.audioSessionId)
    }

    companion object {
        val TAG = MainActivity::class.qualifiedName
    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG)
        toast.show()
    }

}


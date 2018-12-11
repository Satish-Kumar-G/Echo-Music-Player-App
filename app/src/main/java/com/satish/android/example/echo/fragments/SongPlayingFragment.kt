package com.satish.android.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.style.TtsSpan
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.satish.android.example.echo.R
import com.satish.android.example.echo.database.EchoDatabase
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGARTIST
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGDATA
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGID
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGPATH
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGPOS
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGTITLE
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.audioVisualization
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.currentPosition
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.currentSongHelper
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.endTimeTextView
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.fab
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.favoritecontent
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.fetchSongs
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.glview
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.loopImageButton
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.mediaPlayer
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.msensorListener
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.msensorManager
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.myActivity
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.nextImageButton
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.playpauseImageButton
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.previousImageButton
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.seekbar
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.shuffleImageButton
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.songArtistTextView
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.songTitleTextView
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.startTimeTextView
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.temp
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.updateTimeTextVews
import com.satish.android.example.echo.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.satish.android.example.echo.fragments.SongPlayingFragment.Staticated.playNext
import com.satish.android.example.echo.fragments.SongPlayingFragment.Staticated.playPrevious
import com.satish.android.example.echo.fragments.SongPlayingFragment.Staticated.processInformation
import com.satish.android.example.echo.fragments.SongPlayingFragment.Staticated.updateTextViews
import com.satish.android.example.echo.modelClasses.CurrentSongHelper
import com.satish.android.example.echo.modelClasses.Songs
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit


class SongPlayingFragment : Fragment() {




    object Satisfied{
        val MY_PREF_SHUFFLE="Shuffle"
        val MY_PREF_LOOP="Loop"
        //CONSTANTS
        val SONGTITLE = "songTitle"
        val SONGARTIST = "songArtist"
        val SONGID = "songID"
        val SONGPATH = "songPath"
        val SONGDATA = "songData"
        val SONGPOS = "songPosition"

        var myActivity: Activity? = null
        var mediaPlayer: MediaPlayer? = null
        var startTimeTextView: TextView? = null
        var endTimeTextView: TextView? = null
        var songTitleTextView: TextView? = null
        var songArtistTextView: TextView? = null
        var seekbar: SeekBar? = null
        var shuffleImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var playpauseImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var fab:ImageButton?=null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null
        var currentSongHelper: CurrentSongHelper? = null
        var audioVisualization:AudioVisualization?=null
        var glview:GLAudioVisualizationView?=null
        var temp:Boolean =false
        var favoritecontent:EchoDatabase?=null

        var msensorManager:SensorManager?=null
        var msensorListener:SensorEventListener?=null


        var updateTimeTextVews=object: Runnable{
            override fun run() {
                val getCurrent=mediaPlayer?.currentPosition
                startTimeTextView?.setText(String.format("%d:%d",
                        TimeUnit.MINUTES.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent.toLong())- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MINUTES.toMinutes(getCurrent.toLong()))))
                seekbar?.setProgress(getCurrent as Int)
                Handler().postDelayed(this,1000)
            }

        }
    }
    object Staticated{
        fun playNext(check: String) {
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                val randomObject = Random()
                val randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition

            }
            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            val nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.isLoop = false
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songID = nextSong?.songID as Long
            currentSongHelper?.currentPosition = currentPosition

            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            mediaPlayer?.reset()
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(nextSong?.songData))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favoritecontent?.ifExsistInDB(currentSongHelper?.songID?.toInt() as Int) as Boolean){

                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))

            }
        }

        fun playPrevious() {
            currentPosition = currentPosition - 1
            if (currentPosition == -1) {
                currentPosition = 0
            }
            if (currentSongHelper?.isPlaying as Boolean) {
                playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            val nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.isLoop = false
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songID = nextSong?.songID as Long
            currentSongHelper?.currentPosition = currentPosition

            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)

            mediaPlayer?.reset()
            try {
                mediaPlayer?.setDataSource(myActivity, Uri.parse(nextSong?.songData))
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                processInformation(mediaPlayer as MediaPlayer)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (favoritecontent?.ifExsistInDB(currentSongHelper?.songID?.toInt() as Int) as Boolean){

                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))

            }
        }

        fun onSongComplete() {
            if (currentSongHelper?.isShuffle as Boolean) {
                currentSongHelper?.isPlaying = true
                playNext("PlayNextLikeNormalShuffle")
            } else {
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    val nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.isLoop = false
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songID = nextSong?.songID as Long
                    currentSongHelper?.currentPosition = currentPosition

                    updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
                    mediaPlayer?.reset()
                    try {
                        mediaPlayer?.setDataSource(myActivity, Uri.parse(nextSong?.songData))
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformation(mediaPlayer as MediaPlayer)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                } else {
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }
            if (favoritecontent?.ifExsistInDB(currentSongHelper?.songID?.toInt() as Int) as Boolean){

                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))

            }

        }

        fun updateTextViews(songtitle: String, songartist: String) {
            var songTitleUpdated=songtitle
            var songArtistUpdated=songartist
            if (songtitle.equals("<unknown>",true))
            {
                songTitleUpdated="UnKnown Music"
            }
            if (songartist.equals("<unknown>",true))
            {
                songArtistUpdated="Satish Kumar"
            }
            songTitleTextView?.setText(songTitleUpdated)
            songArtistTextView?.setText(songArtistUpdated)
        }
        fun processInformation(mediaplayer:MediaPlayer)
        {
            val finalTime=mediaplayer.duration
            val starttime=mediaplayer.currentPosition
            seekbar?.max=finalTime
            startTimeTextView?.setText(String.format("%d:%d",
                    TimeUnit.MINUTES.toMinutes(starttime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(starttime.toLong())- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MINUTES.toMinutes(starttime.toLong()))))

            endTimeTextView?.setText(String.format("%d:%d",
                    TimeUnit.MINUTES.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong())- TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MINUTES.toMinutes(finalTime.toLong()))))


            seekbar?.setProgress(starttime)
            Handler().postDelayed(updateTimeTextVews,1000)
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_song_playing, container, false)

        setHasOptionsMenu(true)
        activity?.title="Now Playing"
        //Initialisation
        startTimeTextView = v?.findViewById(R.id.startTime)
        endTimeTextView = v?.findViewById(R.id.endTime)
        songTitleTextView = v?.findViewById(R.id.songTitle)
        songArtistTextView = v?.findViewById(R.id.songArtist)
        seekbar = v?.findViewById(R.id.seekBar)
        shuffleImageButton = v?.findViewById(R.id.shuffleButton)
        previousImageButton = v?.findViewById(R.id.previousButton)
        playpauseImageButton = v?.findViewById(R.id.playPauseButton)
        nextImageButton = v?.findViewById(R.id.nextButton)
        loopImageButton = v?.findViewById(R.id.loopButton)
        glview=v?.findViewById(R.id.visualizer_view)
        fab=v.findViewById(R.id.favoriteIcon)
        fab?.alpha=0.8f



        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        audioVisualization=glview as AudioVisualization
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }


    var currentAccleration:Float=0f
    var lastAccelartiom:Float=0f
    var accelaration:Float=0f

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        favoritecontent= EchoDatabase(myActivity)
        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isShuffle = false
        currentSongHelper?.isLoop = false

        var path: String? = null
        var songTitle: String? = null
        var songArtist: String? = null
        var songID: Long = 0
        try {
            path = arguments?.getString(SONGPATH)
            songArtist = arguments?.getString(SONGARTIST)
            songTitle = arguments?.getString(SONGTITLE)
            songID = arguments?.getLong(SONGID) as Long
            currentPosition = arguments?.getInt(SONGPOS) as Int
            fetchSongs = arguments?.getParcelableArrayList(SONGDATA)

            currentSongHelper?.currentPosition = currentPosition
            currentSongHelper?.songArtist = songArtist
            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = songTitle
            currentSongHelper?.songID = songID


            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        var fromFavFragment=arguments?.get("FavBottomBar") as? String
        if (fromFavFragment !=null){
            mediaPlayer=FavoriteFragment.Statified.mediaplayer
        }else {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                if (mediaPlayer?.isPlaying as Boolean) {
                    mediaPlayer?.reset()
                }
                mediaPlayer?.setDataSource(myActivity, Uri.parse(path))
                mediaPlayer?.prepare()
                temp = currentSongHelper?.isLoop as Boolean


            } catch (e: Exception) {
                e.printStackTrace()
            }



            if (mediaPlayer?.isPlaying as Boolean){
                mediaPlayer?.stop()
            }
            mediaPlayer?.start()
        }
        processInformation(mediaPlayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {
            playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaPlayer?.setOnCompletionListener {
            onSongComplete()

        }
        clickListener()
        var visualizationHandler=DbmHandler.Factory.newVisualizerHandler(myActivity as Context,0)
        audioVisualization?.linkTo(visualizationHandler)


        val editorShuffle=myActivity?.getSharedPreferences(Satisfied.MY_PREF_SHUFFLE,Context.MODE_PRIVATE)
        val editorLoop=myActivity?.getSharedPreferences(Satisfied.MY_PREF_LOOP,Context.MODE_PRIVATE)
        val isShuffle=editorShuffle?.getBoolean("feature",false)
        val isLoop=editorLoop?.getBoolean("feature",false)
        if (isShuffle as Boolean)
        {
            currentSongHelper?.isShuffle=true
            currentSongHelper?.isLoop=false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        else{
            currentSongHelper?.isShuffle=false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        if (isLoop as Boolean)
        {
            currentSongHelper?.isShuffle=false
            currentSongHelper?.isLoop=true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        }
        else{
            currentSongHelper?.isLoop=false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }
        if (favoritecontent?.ifExsistInDB(currentSongHelper?.songID?.toInt() as Int) as Boolean){

            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
        }else{
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))

        }




    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible=true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible=false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect -> {
                myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onPause() {
        audioVisualization?.onPause()
        super.onPause()
        msensorManager?.unregisterListener(msensorListener)

    }

    override fun onResume() {
        super.onResume()
        audioVisualization?.onResume()
        msensorManager?.registerListener(msensorListener,
                msensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDestroyView() {
        audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        msensorManager= myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelaration=0.0f
        currentAccleration=SensorManager.GRAVITY_EARTH
        lastAccelartiom=SensorManager.GRAVITY_EARTH
        bindListener()

    }

    fun clickListener() {
        loopImageButton?.setOnClickListener({
            val editorShuffle=myActivity?.getSharedPreferences(Satisfied.MY_PREF_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            val editorLoop=myActivity?.getSharedPreferences(Satisfied.MY_PREF_LOOP,Context.MODE_PRIVATE)?.edit()

            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                currentSongHelper?.isLoop = false
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()

            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorLoop?.putBoolean("feature",true)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
            }

        })
        shuffleImageButton?.setOnClickListener({
            val editorShuffle=myActivity?.getSharedPreferences(Satisfied.MY_PREF_SHUFFLE,Context.MODE_PRIVATE)?.edit()
            val editorLoop=myActivity?.getSharedPreferences(Satisfied.MY_PREF_LOOP,Context.MODE_PRIVATE)?.edit()

            if (currentSongHelper?.isShuffle as Boolean) {
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature",false)
                editorShuffle?.apply()
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)

            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature",false)
                editorLoop?.apply()
                editorShuffle?.putBoolean("feature",true)
                editorShuffle?.apply()

            }

        })
        previousImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isShuffle as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

            }
            playPrevious()

        })
        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }

        })
        playpauseImageButton?.setOnClickListener({
            if (mediaPlayer?.isPlaying as Boolean) {
                playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false

            } else {
                playpauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true

            }
        })
        fab?.setOnClickListener({
            if (favoritecontent?.ifExsistInDB(currentSongHelper?.songID?.toInt() as Int) as Boolean){

                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))
                favoritecontent?.deleteFavorite(currentSongHelper?.songID?.toInt() as Int)
                Toast.makeText(myActivity,"Removed From Favorites",Toast.LENGTH_SHORT).show()
            }else{
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
                favoritecontent?.storeFavoriteSong(currentSongHelper?.songID?.toInt() as Int,
                        currentSongHelper?.songTitle,
                        currentSongHelper?.songArtist,
                        currentSongHelper?.songPath)
                Toast.makeText(myActivity,"Added to Favorites",Toast.LENGTH_SHORT).show()


            }
        })

    }
    fun bindListener(){
        msensorListener=object :SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
                val x= event?.values!![0]
                val y= event.values!![1]
                val z= event.values!![2]

                lastAccelartiom=currentAccleration
                currentAccleration=Math.sqrt((x*x+y*y+z*z).toDouble()).toFloat()
                val delta=currentAccleration-lastAccelartiom
                accelaration=accelaration*0.9f+delta


                if (accelaration>12)
                {
                    val prefix=myActivity?.getSharedPreferences(SettingsFragment.Statified.MY_PREF_NAME,Context.MODE_PRIVATE)
                    val isAllowed=prefix?.getBoolean("feature",false)
                    if (isAllowed as Boolean) {
                        playNext("PlayNextNormal")
                    }

                }


            }

        }

    }


}

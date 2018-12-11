package com.satish.android.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.satish.android.example.echo.R
import com.satish.android.example.echo.adapters.FavoriteAdapter
import com.satish.android.example.echo.database.EchoDatabase
import com.satish.android.example.echo.fragments.FavoriteFragment.Statified.mediaplayer
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGARTIST
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGDATA
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGID
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGPATH
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGPOS
import com.satish.android.example.echo.fragments.SongPlayingFragment.Satisfied.SONGTITLE
import com.satish.android.example.echo.modelClasses.Songs


class FavoriteFragment : Fragment() {
    var myActivity: Activity? = null
    var noFavorites: TextView? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButtton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favData: EchoDatabase? = null
    var refreshList: ArrayList<Songs>? = null
    var listFromDB: ArrayList<Songs>? = null

    object Statified {

        var mediaplayer: MediaPlayer? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_favorite, container, false)
        activity?.title="Favorites"
        noFavorites = v.findViewById(R.id.noFavorites)
        nowPlayingBottomBar = v.findViewById(R.id.hiddenBarFavoriteScreen)
        playPauseButtton = v.findViewById(R.id.playPauseButton)
        recyclerView = v.findViewById(R.id.favoriteRecyclerView)



        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favData = EchoDatabase(myActivity)
        display_favorites_by_searching()
        bottomBarSetUp()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
    }

    fun getSongsFromphone(): ArrayList<Songs> {
        var songsArraylist = ArrayList<Songs>()
        var contentResolver = myActivity?.contentResolver
        var songsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songsCursor = contentResolver?.query(songsUri, null, null, null, null)
        if (songsCursor != null && songsCursor.moveToFirst()) {
            val songID = songsCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songAdded = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songsCursor.moveToNext()) {
                var currentID = songsCursor.getLong(songID)
                var currentTitle = songsCursor.getString(songTitle)
                var currentArtist = songsCursor.getString(songArtist)
                var currentData = songsCursor.getString(songData)
                var currentAddedDate = songsCursor.getLong(songAdded)
                songsArraylist.add(Songs(currentID, currentTitle, currentArtist, currentData, currentAddedDate))
            }
        }



        return songsArraylist
    }

    fun bottomBarSetUp() {
        try {
            bottomBarClickHamdler()
            songTitle?.setText(SongPlayingFragment.Satisfied.currentSongHelper?.songTitle)
            SongPlayingFragment.Satisfied.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongPlayingFragment.Satisfied.currentSongHelper?.songTitle)

                SongPlayingFragment.Staticated.onSongComplete()
            }
            if (SongPlayingFragment.Satisfied.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun bottomBarClickHamdler() {
        nowPlayingBottomBar?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()
            mediaplayer = SongPlayingFragment.Satisfied.mediaPlayer
            val args = Bundle()
            args.putString(SONGARTIST, SongPlayingFragment.Satisfied.currentSongHelper?.songArtist)
            args.putString(SONGTITLE, SongPlayingFragment.Satisfied.currentSongHelper?.songTitle)
            args.putString(SONGPATH, SongPlayingFragment.Satisfied.currentSongHelper?.songPath)
            args.putInt(SONGPOS, SongPlayingFragment.Satisfied.currentSongHelper?.currentPosition as Int)
            args.putLong(SONGID, SongPlayingFragment.Satisfied.currentSongHelper?.songID as Long)
            args.putParcelableArrayList(SONGDATA, SongPlayingFragment.Satisfied.fetchSongs)
            args.putString("FavBottomBar", "sucess")
            songPlayingFragment.arguments = args
            fragmentManager?.beginTransaction()
                    ?.replace(R.id.details_fragment,
                            songPlayingFragment, "Song Playing Fragment")
                    ?.commit()

        }
        playPauseButtton?.setOnClickListener {
            if (SongPlayingFragment.Satisfied.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Satisfied.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Satisfied.mediaPlayer?.getCurrentPosition() as Int
                playPauseButtton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                SongPlayingFragment.Satisfied.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Satisfied.mediaPlayer?.start()
                playPauseButtton?.setBackgroundResource(R.drawable.play_icon)

            }
        }
    }

    fun display_favorites_by_searching() {
        if (favData?.checkSize() as Int > 0) {
            refreshList = ArrayList()
            listFromDB = favData?.queryDBList()
            val fetchList = getSongsFromphone()
            if (fetchList != null) {
                for (i in 0..fetchList.size - 1) {
                    for (j in 0..listFromDB?.size as Int - 1) {
                        if ((listFromDB?.get(j)?.songID) === (fetchList?.get(i)?.songID)) {
                            refreshList?.add((listFromDB as ArrayList<Songs>)[j])
                        }
                    }
                }

            } else {

            }
            if (listFromDB == null) {
                recyclerView?.visibility = View.INVISIBLE
                noFavorites?.visibility = View.VISIBLE
            } else {
                var favAdapter = FavoriteAdapter(myActivity as Context, listFromDB as ArrayList<Songs>)
                var mlayoutmanager = LinearLayoutManager(activity)
                recyclerView?.layoutManager = mlayoutmanager
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favAdapter
                recyclerView?.setHasFixedSize(true)
            }


        } else {
            recyclerView?.visibility = View.INVISIBLE
            noFavorites?.visibility = View.VISIBLE

        }
    }


}

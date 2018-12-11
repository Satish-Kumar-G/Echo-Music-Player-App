package com.satish.android.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.satish.android.example.echo.R
import com.satish.android.example.echo.adapters.MainScreenAdapter
import com.satish.android.example.echo.modelClasses.Songs
import java.util.*


class MainScreenFragment : Fragment() {

    var songsTitle: TextView? = null
    var nowPlayingBottomBar: TextView? = null
    var playPauseButton: ImageButton? = null
    var visibleLayout: RelativeLayout? = null
    var noSongs: RelativeLayout? = null
    var recyclerView: RecyclerView? = null
    var myActivity: Activity? = null
    var getsongsList: ArrayList<Songs>? = null
    var mainScreenAdapter: MainScreenAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
setHasOptionsMenu(true)
        activity?.title="All My Songs"
        visibleLayout = view.findViewById(R.id.visibleLayout)
        recyclerView = view.findViewById(R.id.contentMainRecycler)
        playPauseButton = view.findViewById(R.id.palypauseButton)
        noSongs = view.findViewById(R.id.noSongs)
        songsTitle = view.findViewById(R.id.songTitleMainScreen)
        nowPlayingBottomBar = view.findViewById(R.id.nowPalying)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getsongsList = getSongsFromphone()

        val pref=myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
        val  sort_name=pref?.getString("action_sort_name","true")
        val sort_recent=pref?.getString("action_sort_recent","false")

        if (getsongsList == null){
            visibleLayout?.visibility=View.INVISIBLE
            noSongs?.visibility=View.VISIBLE
        }else{
            mainScreenAdapter = MainScreenAdapter(myActivity as Context, getsongsList as ArrayList<Songs>)
            val layoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = layoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = mainScreenAdapter

        }


        if (getsongsList!=null)
        {
            if (sort_name!!.equals("true",true)){
                Collections.sort(getsongsList,Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }else if(sort_recent!!.equals("true",true)){
                Collections.sort(getsongsList,Songs.Statified.dateComparator)
                mainScreenAdapter?.notifyDataSetChanged()

            }
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        menu?.clear()
        inflater?.inflate(R.menu.main,menu)
        return

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher=item?.itemId
        if (switcher==R.id.action_sort_recent){
            val editor=myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_name","true")
            editor?.putString("action_sort_recent","false")
            editor?.apply()

            if (getsongsList!=null)
            {
                Collections.sort(getsongsList,Songs.Statified.nameComparator)

            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false

        }else if(switcher==R.id.action_sort_name)
        {
            val editor=myActivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_name","false")
            editor?.putString("action_sort_recent","true")
            editor?.apply()
            if (getsongsList!=null)
            {
                Collections.sort(getsongsList,Songs.Statified.dateComparator)

            }
            mainScreenAdapter?.notifyDataSetChanged()
            return false


        }
        return super.onOptionsItemSelected(item)
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

}

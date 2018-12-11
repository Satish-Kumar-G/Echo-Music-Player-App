package com.satish.android.example.echo.adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.satish.android.example.echo.R
import com.satish.android.example.echo.fragments.SongPlayingFragment
import com.satish.android.example.echo.modelClasses.Songs

class MainScreenAdapter(_context: Context, _songsList: ArrayList<Songs>) : RecyclerView.Adapter<MainScreenAdapter.MyViewHolder>() {
    var songsList: ArrayList<Songs>? = null
    var context: Context? = null

    //constants
    val SONGTITLE="songTitle"
    val SONGARTIST="songArtist"
    val SONGID="songID"
    val SONGPATH="songPath"
    val SONGDATA="songData"
    val SONGPOS="songPosition"
    init {
        this.context = _context
        this.songsList = _songsList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.row_custom_mainscreen_adapter, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (songsList == null) {
            return 0
        } else {
            return (songsList as ArrayList<Songs>).size
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val songObject = songsList?.get(position)
        holder.trackTitle?.text = songObject?.songTitle
        holder.trackArtist?.text = songObject?.artist

        holder.itemLayout?.setOnClickListener {

            val songPlayingFragment=SongPlayingFragment()
            val args=Bundle()
            args.putString(SONGARTIST,songObject?.artist)
            args.putString(SONGTITLE,songObject?.songTitle)
            args.putString(SONGPATH,songObject?.songData)
            args.putInt(SONGPOS,position)
            args.putLong(SONGID,songObject?.songID as Long)
            args.putParcelableArrayList(SONGDATA,songsList)
            songPlayingFragment.arguments=args
            (context as FragmentActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.details_fragment,
                            songPlayingFragment,"Song Playing Fragment")
                    .addToBackStack("SongPlayingFragment")
                    .commit()        }

    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        var trackTitle: TextView? = null
        var trackArtist: TextView? = null
        var itemLayout: RelativeLayout? = null

        init {
            itemLayout = itemView?.findViewById(R.id.contentRow)
            trackTitle = itemView?.findViewById(R.id.trackTitle)
            trackArtist = itemView?.findViewById(R.id.trackArtist)

        }

    }


}
package com.satish.android.example.echo.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.satish.android.example.echo.R
import com.satish.android.example.echo.activities.MainActivity
import com.satish.android.example.echo.fragments.SongPlayingFragment

class CaptureBroadCast:BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action==Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Satisfied.notificationManager?.cancel(1999)

            }catch (e:Exception){
                e.printStackTrace()
            }
            try {
                if (SongPlayingFragment.Satisfied.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragment.Satisfied.mediaPlayer?.pause()
                    SongPlayingFragment.Satisfied.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                }

            }catch (e:Exception){
                e.printStackTrace()
            }

        }else{
            val tm:TelephonyManager=context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm.callState){
                TelephonyManager.CALL_STATE_RINGING ->{
                    try {
                        MainActivity.Satisfied.notificationManager?.cancel(1999)

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    try {
                        if (SongPlayingFragment.Satisfied.mediaPlayer?.isPlaying as Boolean) {
                            SongPlayingFragment.Satisfied.mediaPlayer?.pause()
                            SongPlayingFragment.Satisfied.playpauseImageButton?.setBackgroundResource(R.drawable.play_icon)

                        }

                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                else ->{

                }
            }

        }
    }

}
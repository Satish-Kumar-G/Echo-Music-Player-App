package com.satish.android.example.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import com.satish.android.example.echo.R
import com.satish.android.example.echo.fragments.SettingsFragment.Statified.MY_PREF_NAME


class SettingsFragment : Fragment() {
    var myActivity: Activity?=null
    var shakeSwitch: Switch?=null
    object Statified{
        val MY_PREF_NAME="ShakeFeature"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_settings, container, false)
        shakeSwitch=view?.findViewById(R.id.switchShake)
        activity?.title="Settings"
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val pref=myActivity?.getSharedPreferences(MY_PREF_NAME,Context.MODE_PRIVATE)
        val isAllowed=pref?.getBoolean("feature",false)
        if (isAllowed as Boolean){
            shakeSwitch?.isChecked=true

        }else{
            shakeSwitch?.isChecked=false
        }
        shakeSwitch?.setOnCheckedChangeListener({buttonView: CompoundButton?, isChecked: Boolean ->

            if (isChecked)
            {
                val editor=myActivity?.getSharedPreferences(MY_PREF_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.apply()
            }else{
                val editor=myActivity?.getSharedPreferences(MY_PREF_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",false)
                editor?.apply()

            }
        })

    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity=activity
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity=context as Activity
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        var item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
    }


}

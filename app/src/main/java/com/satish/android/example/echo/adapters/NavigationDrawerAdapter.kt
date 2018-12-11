package com.satish.android.example.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.satish.android.example.echo.R
import com.satish.android.example.echo.activities.MainActivity
import com.satish.android.example.echo.fragments.AboutUsFragment
import com.satish.android.example.echo.fragments.FavoriteFragment
import com.satish.android.example.echo.fragments.MainScreenFragment
import com.satish.android.example.echo.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList: ArrayList<String>,_getImages:IntArray,_context:Context)
    : RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    private var mcontext:Context?=null
   private var getcontentlist:ArrayList<String>?=null
   private var getImages:IntArray?=null
    init {
        this.mcontext=_context
        this.getImages=_getImages
        this.getcontentlist=_contentList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder {
        val itemView=LayoutInflater.from(parent.context)
                .inflate(R.layout.row_custom_navdrawer,parent,false)
        return  NavViewHolder(itemView)
    }

    override fun getItemCount(): Int {
       return getcontentlist?.size as Int
    }

    override fun onBindViewHolder(holder: NavViewHolder, position: Int) {
        holder.icon_GET?.setBackgroundResource(getImages?.get(position)as Int)
        holder.text_GET?.setText(getcontentlist?.get(position))
        holder.contentHolder?.setOnClickListener({
            if (position.equals(0)){
                val mainScreenFrgament=MainScreenFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,mainScreenFrgament,"MainScreenFragment")
                        .commit()
            }else if (position.equals(1))
            {val favScreenFragment=FavoriteFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,favScreenFragment,"Favorite Screen Fragment")
                        .commit()

            }else if (position.equals(2))
            {
                val settingFragmnet=SettingsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,settingFragmnet,"Settings Fragment")
                        .commit()
            }else if (position.equals(3))
            {
                val aboutUsFragment=AboutUsFragment()
                (mcontext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,aboutUsFragment,"About Us Fragment")
                        .commit()
            }
            MainActivity.Satisfied.drawerLayout?.closeDrawers()
        })


        }

    class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        var icon_GET: ImageView?=null
        var text_GET: TextView?=null
        var contentHolder:RelativeLayout?=null
        init {
            icon_GET=itemView?.findViewById(R.id.icon_navdrawer)
            text_GET=itemView?.findViewById(R.id.text_navdrawer)
            contentHolder=itemView?.findViewById(R.id.navdrawer_item_content_holder)
        }
    }
}
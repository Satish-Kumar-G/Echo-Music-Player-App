package com.satish.android.example.echo.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.satish.android.example.echo.R

class SplashActivity : AppCompatActivity() {
    var permissionsstring= arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.PROCESS_OUTGOING_CALLS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        if (!hasPermissions(this@SplashActivity, *permissionsstring))
        {
            ActivityCompat.requestPermissions(this@SplashActivity,permissionsstring,100);


        }else
        {
           navigate()
        }
    }
    fun hasPermissions(context: Context,vararg permissions: String): Boolean{
        var  hasallpermissions=true
        for (permission in permissions)
        {
            val res=context.checkCallingOrSelfPermission(permission)
            if (res!=PackageManager.PERMISSION_GRANTED)
            {
                hasallpermissions=false
                ///add the lines of code to give permissions
            }
        }
        return hasallpermissions
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode)
        {
            100->{
                if (grantResults.isNotEmpty()
                        && grantResults[0]==PackageManager.PERMISSION_GRANTED
                        && grantResults[1]==PackageManager.PERMISSION_GRANTED
                        && grantResults[2]==PackageManager.PERMISSION_GRANTED
                        && grantResults[3]==PackageManager.PERMISSION_GRANTED
                        && grantResults[4]==PackageManager.PERMISSION_GRANTED)
                {
                    navigate()
                    this.finish()

                }else
                {
                    Toast.makeText(this@SplashActivity,"Please Grant All Permissions",Toast.LENGTH_SHORT).show()
                    this.finish()
                }
                return
            }
            else->{
                Toast.makeText(this@SplashActivity,"",Toast.LENGTH_SHORT).show()
                this.finish()
                return
            }
        }


    }
    fun navigate(){
        Handler().postDelayed({
            val startact=Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(startact)
            this.finish()
        },1000)

    }
}

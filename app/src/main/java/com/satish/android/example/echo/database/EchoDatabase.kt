package com.satish.android.example.echo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.satish.android.example.echo.database.EchoDatabase.Staticated.COLUMN_ID
import com.satish.android.example.echo.database.EchoDatabase.Staticated.COLUMN_SONG_ARTIST
import com.satish.android.example.echo.database.EchoDatabase.Staticated.COLUMN_SONG_PATH
import com.satish.android.example.echo.database.EchoDatabase.Staticated.COLUMN_SONG_TITLE
import com.satish.android.example.echo.database.EchoDatabase.Staticated.TABLE_NAME
import com.satish.android.example.echo.modelClasses.Songs
import java.lang.Exception

class EchoDatabase:SQLiteOpenHelper{
    var _songsList=ArrayList<Songs>()

    object Staticated{
        val DB_VERSION=1
        val DB_NAME="EchoDataBase"
        val TABLE_NAME="FavoritesTable"
        val COLUMN_ID="SongId"
        val COLUMN_SONG_TITLE="SongTitle"
        val COLUMN_SONG_ARTIST="SongArtist"
        val COLUMN_SONG_PATH="SongPath"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
        "CREATE TABLE "+TABLE_NAME+" ( "+COLUMN_ID+" INTEGER,"+COLUMN_SONG_TITLE+" TEXT,"
        +COLUMN_SONG_ARTIST+" TEXT,"+COLUMN_SONG_PATH+" TEXT);")


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)

    fun storeFavoriteSong(id:Int?,title:String?,artist:String?,path:String?){
        val db=this.writableDatabase
        val contentValues=ContentValues()
        contentValues.put(COLUMN_ID,id)
        contentValues.put(COLUMN_SONG_TITLE,title)
        contentValues.put(COLUMN_SONG_ARTIST,artist)
        contentValues.put(COLUMN_SONG_PATH,path)
        db.insert(TABLE_NAME,null,contentValues)
        db.close()
    }
    fun queryDBList():ArrayList<Songs>?{
        try {
            val db=this.readableDatabase
            val query="SELECT * FROM "+TABLE_NAME
            val cursor=db.rawQuery(query,null) as Cursor
            if (cursor.moveToFirst())
            {
                do{
                    val _title=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_TITLE))
                    val _id=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val _artist=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST))
                    val _path=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SONG_PATH))
                    _songsList.add(Songs(_id.toLong(),_title,_artist,_path,0))

                }while (cursor.moveToNext())
            }else{
                return null
            }

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
        return _songsList

    }
    fun ifExsistInDB(_id:Int):Boolean
    {
        var storedId=-1090
        val db=this.readableDatabase
        val query="SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_ID+"= '$_id'"
        val cursor=db.rawQuery(query,null)
        if (cursor.moveToFirst())
        {
            do {
                storedId=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))

            }while (cursor.moveToNext())

        }
        else{
            return false
        }
        return storedId !=-1090

    }
    fun deleteFavorite(_id:Int){
        val db=this.writableDatabase
        db.delete(TABLE_NAME,COLUMN_ID+" ="+_id,null)
        db.close()
    }
    fun checkSize():Int{
        var counter=0
        val db=this.readableDatabase
        val query="SELECT * FROM "+TABLE_NAME
        val cursor=db.rawQuery(query,null)
        if (cursor.moveToFirst())
        {
            do {
                counter=counter+1

            }while (cursor.moveToNext())

        }
        else{
            return 0
        }
        return counter

    }
}

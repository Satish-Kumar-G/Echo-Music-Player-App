package com.satish.android.example.echo.modelClasses

import android.os.Parcel
import android.os.Parcelable

class Songs(var songID:Long,
            var songTitle:String,
            var artist:String,
            var songData:String,
            var dateAdded:Long):Parcelable
{
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeLong(songID)
        dest?.writeString(songTitle)
        dest?.writeString(artist)
        dest?.writeString(songData)
        dest?.writeLong(dateAdded)
    }

    override fun describeContents(): Int {
return 0
    }

    companion object CREATOR : Parcelable.Creator<Songs> {
        override fun createFromParcel(parcel: Parcel): Songs {
            return Songs(parcel)
        }

        override fun newArray(size: Int): Array<Songs?> {
            return arrayOfNulls(size)
        }
    }
    object Statified{
        var nameComparator: Comparator<Songs> = Comparator<Songs> { o1, o2 ->

            val songOne=o1.songTitle.toUpperCase()
            val songTo=o2.songTitle.toUpperCase()
            songOne.compareTo(songTo)

        }
        var dateComparator: Comparator<Songs> = Comparator { o1, o2 ->
            val songOne=o1.dateAdded.toDouble()
            val songTwo=o2.dateAdded.toDouble()
            songTwo.compareTo(songOne)
        }
    }

}
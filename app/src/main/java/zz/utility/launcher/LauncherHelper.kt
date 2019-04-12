package zz.utility.launcher

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.View
import android.widget.TextView
import zz.utility.R

data class AppInfo(
        val label: String,
        val packageName: String
)

fun CharSequence.firstLetters(): String {
    val a = this.split(" ")
    return when (a.size) {
        0 -> "-"
        1 -> "${a[0].first()}"
        else -> "${a[0].first()}${a[1].first()}"
    }
}

class ViewHolder(val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(R.id.title)
    val img: TextView = view.findViewById(R.id.img)
    val subtitle: TextView = view.findViewById(R.id.subtitle)
}

fun getAppIcon(mPackageManager: PackageManager, packageName: String): Bitmap? {

    try {
        val drawable = mPackageManager.getApplicationIcon(packageName)

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && drawable is AdaptiveIconDrawable) {
            val backgroundDr = drawable.background
            val foregroundDr = drawable.foreground

            val drr = arrayOfNulls<Drawable>(2)
            drr[0] = backgroundDr
            drr[1] = foregroundDr

            val layerDrawable = LayerDrawable(drr)

            val width = layerDrawable.intrinsicWidth
            val height = layerDrawable.intrinsicHeight

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bitmap)

            layerDrawable.setBounds(0, 0, canvas.width, canvas.height)
            layerDrawable.draw(canvas)

            return bitmap
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    return null
}
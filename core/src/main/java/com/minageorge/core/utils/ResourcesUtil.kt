package com.minageorge.core.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

class ResourcesUtil(var context: Context) {

    fun getString(@StringRes resourceId: Int) = context.getString(resourceId)

    fun getColor(@ColorRes resourceId: Int) = ContextCompat.getColor(context, resourceId)

    fun getDrawable(@DrawableRes resourceId: Int) = ContextCompat.getDrawable(context, resourceId)

    fun getLayoutInflater(): LayoutInflater = LayoutInflater.from(context)

    fun isOrientationPortrait() = Configuration.ORIENTATION_PORTRAIT == context.resources.configuration.orientation

    fun isEnglish() = true //TODO under develop

    fun loadBitmapFromView(view: View): Bitmap {
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

}
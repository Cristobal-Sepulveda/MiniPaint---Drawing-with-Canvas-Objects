package com.example.android.minipaint

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

//TODO 4.1
private const val STROKE_WIDTH = 12f // has to be float

class MyCanvasView(context: Context) : View(context) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    //TODO 4.2
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    //TODO 4.6
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    //TODO 4.8
    private var currentX = 0f
    private var currentY = 0f

    //TODO 4.10
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //TODO 4.3
    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    //TODO 4.4
    private var path = Path()

    //TODO 4.7
    private fun touchStart() {
        //TODO 4.9
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }
    private fun touchMove() {
        //TODO 4.11
        //1) first we calculate the distance that has been moved, that's dx dy
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)
        //2) if the movement was further than the touchTolerance, add a segment to the path
        if (dx >= touchTolerance || dy >= touchTolerance) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
                //using quadTo instead of lineTo, create a smoothly drawn line without corners
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            //3) next, set the starting point for the next segment to the end point of this segment
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }
        //call invalidate to force redrawing of the screen with the updated path (to eventually call
        //onDraw and redraw the view
        invalidate()

    }
    private fun touchUp() {
        //TODO 4.12
        path.reset()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    //TODO 4.5
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }



}

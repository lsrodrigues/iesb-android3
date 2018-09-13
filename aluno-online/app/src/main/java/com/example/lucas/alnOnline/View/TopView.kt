package com.example.lucas.alnOnline.View

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class TopView: View {
    private var currentIndex: Int = 0
    public var photoResourcesIndex: Array<Int> = emptyArray()
    public var transitionTime: Long = 3000
    constructor(context: Context?): super(context, null)
    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr) {

    }

    private fun drawBackGroundRec(canvas: Canvas) {
        val paint = Paint()
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = Color.RED
        paint.strokeWidth = 1f

        val backRec = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())

        canvas.drawRect(backRec, paint)
    }

    private fun drawPhoto(resourceIndex: Int, canvas: Canvas) {
        val paint = Paint()
        val originBmp = BitmapFactory.decodeResource(resources, resourceIndex)
        val widthRatio = this.width.toFloat()/originBmp.width.toFloat()
        val heightRatio = this.height.toFloat()/originBmp.height.toFloat()
        val resizeMatrix = Matrix()
        resizeMatrix.postScale(widthRatio,heightRatio)
        val resizedBmp = Bitmap.createBitmap(originBmp, 0, 0, originBmp.width, originBmp.height, resizeMatrix, true)
        canvas.drawBitmap(resizedBmp, 0f, 0f, paint)
    }

    private fun nextIndex(): Int {
        return (currentIndex + 1) % photoResourcesIndex.count()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (photoResourcesIndex.count() > 0) {
            Log.d("Debug","Com Fotos")
            drawPhoto(photoResourcesIndex[currentIndex], canvas!!)
            currentIndex = nextIndex()
        } else {
            Log.d("Debug","Sem Fotos")
            drawBackGroundRec(canvas!!)
        }
        postInvalidateDelayed(transitionTime)
    }

}

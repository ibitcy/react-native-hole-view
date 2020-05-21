package com.ibitcy.react_native_hole_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class RNHoleView: FrameLayout {

    class Hole(var x: Int, var y: Int, var width: Int, var height: Int, var borderRadius: Int = 0)

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var holesPath: Path? = null
    private val holesPaint = Paint()

    var bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)


    init {
        setClickable(false)
        setFocusable(false)

        holesPaint.isAntiAlias = true;
        holesPaint.color = resources.getColor(android.R.color.transparent)
        holesPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);

        postDelayed({
            val c = Canvas(bitmap)
            draw(c)
            //canvas?.drawBitmap(bitmap, 0.0f, 0.0f, null)
            val mByteArrayOutputStream = ByteArrayOutputStream()
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, mByteArrayOutputStream)
                bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(mByteArrayOutputStream.toByteArray()))
                FileOutputStream(File(context.getExternalFilesDir(null), "test" + 1 + ".png")).use { outputStream -> mByteArrayOutputStream.writeTo(outputStream) }
                mByteArrayOutputStream.flush()
                mByteArrayOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 10000)
    }

    fun addHole(hole: Hole) {
        Log.d("12345", "add hole ${hole.borderRadius}")
        val path = Path()
        path.addRoundRect(RectF(
                hole.x.toFloat(),
                hole.y.toFloat(),
                hole.width.toFloat(),
                hole.height.toFloat()),
                hole.borderRadius.toFloat(),
                hole.borderRadius.toFloat(),
                Path.Direction.CW
        )
        if (holesPath == null) {
            holesPath = path
        } else {
            holesPath!!.addPath(path)
        }

        path.addRoundRect(RectF(
                0.0f,
                0.0f,
                this.width.toFloat(),
                this.height.toFloat()),
                0.0f,
                0.0f,
                Path.Direction.CW
        )

        postInvalidate()
    }

    var counter = 0

    override fun onDraw(canvas: Canvas?) {
//        counter++
        super.onDraw(canvas)

//        if (holesPath != null) {
//            Log.d("12345", "onDraw")
//            canvas?.drawPath(holesPath!!, holesPaint)
//        }

//        val mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
//        canvas?.drawBitmap(mBitmap, 0.0f,0.0f,null);
        val path = Path()
        path.fillType=Path.FillType.EVEN_ODD;

        path.addRoundRect(RectF(
                20.0f,
                20.0f,
                100.0f,
                100.0f),
                0.0f,
                0.0f,
                Path.Direction.CW
        )

        path.addRoundRect(RectF(
                100.0f,
                100.0f,
                200.0f,
                200.0f),
                0.0f,
                0.0f,
                Path.Direction.CW
        )

        path.addRoundRect(RectF(
                0.0f,
                0.0f,
                500.0f,
                500.0f),
                0.0f,
                0.0f,
                Path.Direction.CW
        )

        val testPaint= Paint()
        testPaint.isAntiAlias = true;
//        testPaint.color = resources.getColor(android.R.color.transparent)
        testPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR);


//        val bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
//        canvas?.drawPaint(testPaint)
        canvas?.drawPath(path, testPaint);
//        canvas?.drawCircle(10.0f,10.0f,20.0f, testPaint);

//       val mCanvas = Canvas(mBitmap);
//        mCanvas.drawColor(getResources().getColor(R.color.black_overlay));
//        mCanvas.drawCircle(-3*(getHeight())/4, (getHeight())/2, getHeight(), mPaint);
//        canvas.drawBitmap(mBitmap, 0, 0, null);

//        canvas.draw
    }

//    override fun dispatchDraw(canvas: Canvas?) {
//        if (holesPath != null) {
//            Log.d("12345", "clipPath")
//            canvas?.clipPath(holesPath!!)
//        }
//
//        super.dispatchDraw(canvas)
//    }

    override fun performClick(): Boolean {
        return false;
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false;
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return false;
    }

    override fun onInterceptHoverEvent(event: MotionEvent?): Boolean {
        return false;
    }
}
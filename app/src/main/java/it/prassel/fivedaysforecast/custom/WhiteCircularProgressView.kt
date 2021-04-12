package it.prassel.fivedaysforecast.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Created by ivan on 27/03/16.
 */
class WhiteCircularProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val mDrawable: CircularProgressDrawable?

    //Public Methods


    var color = Color.WHITE
        set(color) {
            field = color
            if (mDrawable != null) {
                mDrawable.color = color
            }
        }

    init {

        val width = 5f // Add the width you want
        val unit = TypedValue.COMPLEX_UNIT_DIP // In the units you want it
        // Remember this is the ring's line, not the whole view.
        val result = TypedValue.applyDimension(unit, width, resources.displayMetrics)

        mDrawable = CircularProgressDrawable(Color.WHITE, 4f)
        mDrawable.callback = this

        if (visibility == View.VISIBLE) {
            mDrawable.start()
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == View.VISIBLE) {
            mDrawable?.start()
        } else {
            mDrawable?.stop()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mDrawable!!.setBounds(0, 0, w, h)
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        mDrawable!!.draw(canvas)
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return who === mDrawable || super.verifyDrawable(who)
    }
}


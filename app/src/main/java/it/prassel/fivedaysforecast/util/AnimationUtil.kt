package it.prassel.fivedaysforecast.util

import android.animation.Animator

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import it.prassel.fivedaysforecast.BadContextException
import it.prassel.fivedaysforecast.R


/**
 * Created by ivan on 22/11/16.
 */

object AnimationUtil {

    private val BOTTOM_PANEL_IN_ANIMATION_TIME: Long = 300
    private val BOTTOM_PANEL_OUT_ANIMATION_TIME: Long = 200
    private val SIDE_PANEL_IN_ANIMATION_TIME: Long = 200
    private val SIDE_PANEL_OUT_ANIMATION_TIME: Long = 200
    private val OVERLAY_BG = "#4D000000"
    val OVERLAY_FADE_TIME = 200
    private val TAG = "AnimationUtil"

    val OVL_FADE_TIME = 200


    fun initBottomPanel(panel: View) {

        val dInfo = DeviceInfo()
        val h = dInfo.height
        panel.y = h.toFloat()

    }

    fun showBottomPanel(panel: View) {
        panel.animate().y(0.0f).duration = BOTTOM_PANEL_IN_ANIMATION_TIME
    }

    fun showBottomPanel(panel: View, withAnimation: Boolean) {
        if (withAnimation) {
            panel.animate().y(0.0f).duration = BOTTOM_PANEL_IN_ANIMATION_TIME
        } else {
            panel.y = 0.0f
        }
    }

    fun hideBottomPanel(panel: View) {
        val dInfo = DeviceInfo()
        val h = dInfo.height
        panel.animate().y(h.toFloat()).duration = BOTTOM_PANEL_OUT_ANIMATION_TIME
    }

    fun hideBottomPanel(panel: View, withAnimation: Boolean) {
        val dInfo = DeviceInfo()
        val h = dInfo.height
        if (withAnimation == true) {
            panel.animate().y(h.toFloat()).duration = BOTTOM_PANEL_OUT_ANIMATION_TIME
        } else {
            panel.y = h.toFloat()
        }
    }

    fun hideBottomPanelWithoffset(panel: View, withAnimation: Boolean, offset: Float = 0.0f) {
        val dInfo = DeviceInfo()
        val h = dInfo.height
        Log.i(TAG,"-- <AnimationUtil> hideBottomPanelWithoffset view size: ${panel.y}")
        if (withAnimation == true) {
            panel.animate().y(panel.y + offset).duration = BOTTOM_PANEL_OUT_ANIMATION_TIME
        } else {
            panel.y = h.toFloat()
        }
    }


    fun hideBottomPanel(panel: View, pixelSize: Int, withAnimation: Boolean) {
        val dInfo = DeviceInfo()
        if (withAnimation == true) {
            panel.animate().y(pixelSize.toFloat()).duration = BOTTOM_PANEL_OUT_ANIMATION_TIME
        } else {
            panel.y = pixelSize.toFloat()
        }
    }

    fun initLeftPanel(panel: View) {

        val dInfo = DeviceInfo()
        val w = dInfo.width
        val offset = w * -1
        panel.x = offset.toFloat()

    }

    fun hideLeftPanel(panel: View, withAnimation: Boolean) {
        val dInfo = DeviceInfo()
        val w = dInfo.width
        val offset = w * -1
        if (withAnimation) {
            panel.animate().x(offset.toFloat()).duration = SIDE_PANEL_OUT_ANIMATION_TIME
        } else {
            panel.x = offset.toFloat()
        }
    }

    fun hideRightPanel(panel: View, withAnimation: Boolean) {
        val dInfo = DeviceInfo()
        val deviceW = dInfo.width
        val viewW = panel.width
        Log.i(TAG, "-- <AnimationUtil> hideRightPanel viewW: $viewW")
        if (withAnimation) {
            panel.animate().x(deviceW.toFloat()).duration = SIDE_PANEL_OUT_ANIMATION_TIME
        } else {
            panel.x = deviceW.toFloat()
        }
    }

    fun showRightPanel(panel: View, withAnimation: Boolean, position: Int? = null) {
        val dInfo = DeviceInfo()
        val deviceW = dInfo.width
        val viewW = panel.width
        val offset = deviceW - viewW
        Log.i(TAG, "-- <AnimationUtil> showRightPanel viewW: $offset")

        val pos = position ?: 0
        if (withAnimation) {
            panel.animate().x(pos.toFloat()).duration = SIDE_PANEL_OUT_ANIMATION_TIME
        } else {
            panel.x = offset.toFloat()
        }
    }

    fun hideLeftPanel(panel: View) {
        val dInfo = DeviceInfo()
        val w = dInfo.width
        val offset = w * -1
        panel.animate().x(offset.toFloat()).duration = SIDE_PANEL_OUT_ANIMATION_TIME
    }

    fun showLeftPanel(panel: View) {
        panel.animate().x(0.0f).duration = SIDE_PANEL_IN_ANIMATION_TIME
    }


    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = activity.currentFocus
        if (imm != null && currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

    fun hideKeyboard(dialog: Dialog) {
        val imm = dialog.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocus = dialog.currentFocus
        if (imm != null && currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }


    fun startIntentWithSlideInRightAnimation(activity: Activity, intent: Intent, extras: Bundle?) {

        if (extras != null) {
            intent.putExtras(extras)
        }
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity)

    }

    fun startIntentWithSlideInRightAnimation4Result(activity: Activity, intent: Intent, extras: Bundle?, requestCode: Int) {

        if (extras != null) {
            intent.putExtras(extras)
        }
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity)

    }


    fun startIntentWithSlideInRightAnimation4Result(frag: Fragment, intent: Intent, extras: Bundle?, requestCode: Int) {
        if (extras != null) {
            intent.putExtras(extras)
        }
        frag.startActivityForResult(intent, requestCode)
        frag.activity!!.overridePendingTransition(R.anim.move_right_in_activity, R.anim.move_left_out_activity)
    }


    fun showErrorSnackBar(rootView: View, s: String) {

        val snackbar = Snackbar.make(rootView, s, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
        val sbView = snackbar.view
        sbView.setBackgroundColor(Color.RED)
        val tv = sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        tv?.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun showPositiveSnackBar(rootView: View, s: String) {

        val snackbar = Snackbar.make(rootView, s, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(rootView.context, R.color.colorGreen))
        val tv = sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        tv?.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun showGenericComunicationErrorSnackBar(rootView: View, ctx: Context) {
        try {
            Util.assertContext(ctx)
        } catch (e: BadContextException) {
            e.printStackTrace()
            return
        }

        val errMsg = ctx.getString(R.string.err_generic_comunication_error)

        val snackbar = Snackbar.make(rootView, errMsg, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
        val sbView = snackbar.view
        sbView.setBackgroundColor(Color.RED)
        val tv = sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        tv?.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun showErrorSnackBar(rootView: View, s: String, lenght: Int) {

        val snackbar = Snackbar.make(rootView, s, lenght)
                .setAction("Action", null)
        val sbView = snackbar.view
        sbView.setBackgroundColor(Color.RED)
        val tv = sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        tv?.setTextColor(Color.WHITE)
        snackbar.show()
    }

//    fun expandAccordion(accordion: ExpandableLinearLayout, accordionArrow: View) {
//        accordion.expand()
//        accordionArrow.animate().rotation(180.0f).duration = 200
//    }
//
//    fun collapseAccordion(accordion: ExpandableLinearLayout, accordionArrow: View) {
//        accordion.collapse()
//        accordionArrow.animate().rotation(0.0f).duration = 200
//    }
//
//    fun manageAccordion(accordion: ExpandableLinearLayout, accordionArrow: View) {
//        if (accordion.isExpanded) {
//            collapseAccordion(accordion, accordionArrow)
//        } else {
//            expandAccordion(accordion, accordionArrow)
//        }
//    }

    fun hideOverlay(ovl: View?, duration: Int) {
        if (ovl != null) {
            Log.v(TAG, "-- hideOverlay")
            ovl.animate().alpha(0.0f).setDuration(duration.toLong()).setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    ovl.visibility = View.GONE
                    ovl.animate().setListener(null)
                    ovl.setOnClickListener(null)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
        }
    }

    fun showOverlay(ovl: View?, duration: Int) {
        if (ovl != null) {
            Log.v(TAG, "-- showOverlay")
            ovl.visibility = View.VISIBLE
            ovl.setBackgroundColor(Color.parseColor(OVERLAY_BG))
            ovl.animate().alpha(1.0f).duration = duration.toLong()
            ovl.setOnClickListener { }
        }
    }

    fun showOverlay(ovl: View?, duration: Int, color: String) {
        if (ovl != null) {
            Log.v(TAG, "-- showOverlay")
            ovl.visibility = View.VISIBLE
            ovl.setBackgroundColor(Color.parseColor(color))
            ovl.animate().alpha(1.0f).duration = duration.toLong()
            ovl.setOnClickListener { }
        }
    }

    fun showOverlayLight(ovl: View?, duration: Int) {
        if (ovl != null) {
            Log.v(TAG, "-- showOverlay")
            ovl.visibility = View.VISIBLE
            ovl.setBackgroundColor(Color.TRANSPARENT)
            ovl.animate().alpha(1.0f).duration = duration.toLong()
            ovl.setOnClickListener { }
        }
    }


}

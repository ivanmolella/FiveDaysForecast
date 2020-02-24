package it.prassel.fivedaysforecast

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.prassel.fivedaysforecast.model.ListItem
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.frag_forecast.*

import java.io.IOException


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForecastDayFragment.OnCompleteFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ForecastDayFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ForecastDayFragment : Fragment() {

    private val TAG = "ForecastDayFragment"


    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mRootView: View? = null
    private var image : ImageView? = null

    private var temp : TextView? = null
    private var city : TextView? = null
    private var weather : TextView? = null

    private var mListener: OnCompleteFragmentInteractionListener? = null

    private val isFragmentAlive: Boolean
        get() = activity != null && isAdded == true

    override fun onStop() {

        super.onStop()
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)



    }

    fun isActivityAliveAndAttached(activity: Activity?, frag: Fragment): Boolean {
        return activity != null && activity.isFinishing == false && frag.isDetached == false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.frag_forecast, container, false)
        image = mRootView?.findViewById(R.id.forecast_image)

        temp=mRootView?.findViewById(R.id.temp)
        city=mRootView?.findViewById(R.id.city)
        weather=mRootView?.findViewById(R.id.weather)

        if (isActivityAliveAndAttached(activity, this)) {
            initFragment()
        }

        return mRootView
    }

    fun fahrenheitToClesius(fahrenheit : Double) : Double{
        return (fahrenheit - 32) / 1.8000
    }


    private fun initFragment() {

        val args = arguments
        val forecast4Day = args?.getSerializable(ARG_PARAM1) as ListItem?
        val city = args?.getString(ARG_PARAM2) as String?

        Log.i(TAG,"-- <ForecastDayFragment> forecast4Day: $forecast4Day")

        when( forecast4Day?.weather!![0]?.id) {
            in 200..299 -> {loadThunder(forecast4Day,city)}
            in 300..399 -> {loadDrizzle(forecast4Day,city)}
            in 500..599 -> {loadRain(forecast4Day,city)}
            in 600..699 -> {loadSnow(forecast4Day,city)}
            in 700..800 -> {loadClear(forecast4Day,city)}
            in 801..899 -> {loadClouds(forecast4Day,city)}
        }
    }

    fun loadImageFromAssets(imageName : String){
        try {
            val ims = activity?.getAssets()?.open(imageName)
            val d = Drawable.createFromStream(ims, null)
            image?.setImageDrawable(d)
        } catch (ex: IOException) {
            return
        }

    }

    private fun loadData(forecast4Day: ListItem,city : String?) {
        temp?.text="${forecast4Day.main!!.temp!!.toInt()!!}°"
        this.city?.text=city
        this.weather?.text= forecast4Day.weather?.get(0)!!.description?.capitalize()
    }

    private fun loadThunder(forecast4Day: ListItem, city : String?) {
        loadImageFromAssets(THUNDER)
        loadData(forecast4Day,city)
    }

    private fun loadDrizzle(forecast4Day: ListItem,city : String?) {
        loadImageFromAssets(DRIZZLE)
        loadData(forecast4Day,city)
    }

    private fun loadRain(forecast4Day: ListItem,city : String?) {
        loadImageFromAssets(RAIN)
        loadData(forecast4Day,city)
    }

    private fun loadSnow(forecast4Day: ListItem,city : String?) {
        loadImageFromAssets(SNOW)
        loadData(forecast4Day,city)
    }

    private fun loadClouds(forecast4Day: ListItem,city : String?) {
        loadImageFromAssets(CLOUDS)
        loadData(forecast4Day,city)
    }

    private fun loadClear(forecast4Day: ListItem,city : String?) {
        loadImageFromAssets(CLEAR)
        loadData(forecast4Day,city)
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(operation: Int) {
        if (mListener != null) {
            mListener!!.onCompleteFragmentInteraction(operation)
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnCompleteFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onCompleteFragmentInteraction(operation: Int)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        const val THUNDER  = "thunder.jpg"
        const val DRIZZLE  = "drizzle.jpg"
        const val RAIN  = "rain.jpg"
        const val SNOW  = "snow.jpg"
        const val CLOUDS  = "clouds.jpg"
        const val CLEAR  = "clear.jpg"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompleteLoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: ListItem?, param2: String?): ForecastDayFragment {
            val fragment = ForecastDayFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

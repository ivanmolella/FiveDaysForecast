package it.prassel.fivedaysforecast

import android.app.Activity
import android.content.Context
import android.graphics.Color
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.prassel.fivedaysforecast.util.Util
import it.prassel.kotlin.vimsmobile.util.DateUtil


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

    private var forecastList : RecyclerView? = null

    private var mListener: OnCompleteFragmentInteractionListener? = null

    private val isFragmentAlive: Boolean
        get() = activity != null && isAdded == true

    private var adapter : RecyclerViewAdapter? = null

    val weatherAvg : MutableMap<Int,Int> = mutableMapOf()


    inner class HolderItem {

        var weatherItem: ListItem? = null

        constructor(wi: ListItem) {
            this.weatherItem = wi
        }

        constructor() {}

    }

    inner class RecyclerViewAdapter(private val mContext: Context, private var modelList: List<HolderItem>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        fun updateList(modelList: ArrayList<HolderItem>) {
            this.modelList = modelList
            notifyDataSetChanged()

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {

            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_weather, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            //Here you can fill your row view
            if (holder is RecyclerViewAdapter.ViewHolder) {

                val item = modelList!![position]
                //holder.image?.setImageResource(R.drawable.ic_launcher_foreground)
                holder.desc?.text="${Util.capitalizeEachWord(item.weatherItem?.weather!![0]?.description)}"
                holder.temp?.text="${item.weatherItem?.main?.temp?.toInt()}°"
                holder.umidity?.text="${item.weatherItem?.main?.humidity}%"
                holder.wind?.text="${item.weatherItem?.wind?.speed.toString()} Km/h"

                val time = DateUtil.formatDate("yyyy-MM-dd HH:mm:ss","HH:mm",item?.weatherItem?.dtTxt!!)
                holder.time?.text=time

                setWeatherImage(holder,item)
            }
        }

        private fun setWeatherImage(holder: ForecastDayFragment.RecyclerViewAdapter.ViewHolder, item: ForecastDayFragment.HolderItem) {

            when( item?.weatherItem?.weather!![0]?.id) {
                in 200..299 -> {loadThunderThnb(holder)}
                in 300..399 -> {loadDrizzleThnb(holder)}
                in 500..599 -> {loadRainThnb(holder)}
                in 600..699 -> {loadSnowThnb(holder)}
                in 700..800 -> {loadClearThnb(holder)}
                in 801..899 -> {loadCloudsThnb(holder)}
            }
        }

        private fun loadThunderThnb(holder: ViewHolder) {
            holder?.image?.setImageResource(R.drawable.ic_thunder)
            holder?.image?.setColorFilter(Color.argb(255, 255, 255, 255));
        }

        private fun loadDrizzleThnb(holder: ViewHolder) {
            holder?.image?.setImageResource(R.drawable.ic_drizzle)
            holder?.image?.setColorFilter(Color.argb(255, 255, 255, 255));
        }

        private fun loadRainThnb(holder: ViewHolder) {
            holder?.image?.setImageResource(R.drawable.ic_rain)
            holder?.image?.setColorFilter(Color.argb(255, 255, 255, 255));
        }

        private fun loadSnowThnb(holder: ViewHolder) {
            holder?.image?.setImageResource(R.drawable.ic_snow)
            holder?.image?.setColorFilter(Color.argb(255, 255, 255, 255));
        }

        private fun loadCloudsThnb(holder: ViewHolder) {
            holder?.image?.setImageResource(R.drawable.ic_clouds)
            holder?.image?.setColorFilter(Color.argb(255, 255, 255, 255));
        }

        private fun loadClearThnb(holder: ViewHolder) {
            holder?.image?.setImageResource(R.drawable.ic_clear)
            holder?.image?.setColorFilter(Color.argb(255, 255, 255, 255));
        }


        override fun getItemCount(): Int {
            return modelList!!.size
        }


        private fun getItem(position: Int): HolderItem {
            return modelList!![position]
        }


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            var image : ImageView? = null
            var temp : TextView? = null
            var time : TextView? = null
            var desc : TextView? = null
            var umidity : TextView? = null
            var wind : TextView? = null

            init {
                image = itemView.findViewById(R.id.row_weather_image)
                time = itemView.findViewById(R.id.row_weather_time)
                temp = itemView.findViewById(R.id.row_weather_temp)
                desc = itemView.findViewById(R.id.row_weather_desc)
                umidity = itemView.findViewById(R.id.row_weather_umidity)
                wind = itemView.findViewById(R.id.row_weather_wind)
            }
        }

    }


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

        forecastList=mRootView?.findViewById(R.id.forecast_in_day)

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
        val weatherItem = args?.getSerializable(ARG_PARAM2) as Array<ListItem>?
        val city = args?.getString(ARG_PARAM3) as String?

        Log.i(TAG,"-- <ForecastDayFragment> forecast4Day: $forecast4Day")

        weatherItem?.forEach {
            Log.i(TAG,"-- <ForecastDayFragment> forecast weather item 4 day: ${it.dtTxt}")
        }
        forecast4Day?.weather?.forEach {
            Log.i(TAG,"-- <ForecastDayFragment> weather: $it")
            val counter = weatherAvg[it!!.id]
            if (counter == null){
                weatherAvg[it!!.id!!]=0
            }
            weatherAvg[it!!.id!!] =  weatherAvg[it!!.id!!]!!.plus(1)
        }


        val maxWeather = weatherAvg.maxBy { it.value }?.key

        Log.i(TAG,"-- <ForecastDayFragment> forecast weatherAvg: $weatherAvg" + " max: ${maxWeather}")


        when( maxWeather ) {
            in 200..299 -> {loadThunder(forecast4Day,city)}
            in 300..399 -> {loadDrizzle(forecast4Day,city)}
            in 500..599 -> {loadRain(forecast4Day,city)}
            in 600..699 -> {loadSnow(forecast4Day,city)}
            in 700..800 -> {loadClear(forecast4Day,city)}
            in 801..899 -> {loadClouds(forecast4Day,city)}
        }

        setForecastInDay(weatherItem)
    }

    private fun setForecastInDay(weatherItem: Array<ListItem>?) {
        val itemlList : List<HolderItem> = buildHolderItemList(weatherItem)
        adapter = RecyclerViewAdapter(activity!!,itemlList)
        val layoutManager = LinearLayoutManager(activity,RecyclerView.VERTICAL,false);
        forecastList?.layoutManager=layoutManager
        forecastList?.adapter=adapter

    }

    private fun buildHolderItemList(weatherItem: Array<ListItem>?): List<ForecastDayFragment.HolderItem> {
        var itemList : MutableList<HolderItem> = mutableListOf();

        weatherItem?.forEach {
            itemList.add(HolderItem(it))
        }

        return itemList
    }

    fun loadImageFromAssets(imageName : String, image : ImageView){
        try {
            val ims = activity?.getAssets()?.open(imageName)
            Log.i(TAG,"-- <ForecastDayFragment> ims: $ims")
            val d = Drawable.createFromStream(ims, null)
            image?.setImageDrawable(d)
        } catch (ex: Throwable) {
            return
        }

    }

    private fun loadData(forecast4Day: ListItem?,city : String?) {
        temp?.text="${forecast4Day?.main!!.temp!!.toInt()!!}°"
        this.city?.text=city
        this.weather?.text= forecast4Day.weather?.get(0)!!.description?.capitalize()
    }

    private fun loadThunder(forecast4Day: ListItem?, city : String?) {
        loadImageFromAssets(THUNDER,image!!)
        loadData(forecast4Day,city)
    }

    private fun loadDrizzle(forecast4Day: ListItem?,city : String?) {
        loadImageFromAssets(DRIZZLE,image!!)
        loadData(forecast4Day,city)
    }

    private fun loadRain(forecast4Day: ListItem?,city : String?) {
        loadImageFromAssets(RAIN,image!!)
        loadData(forecast4Day,city)
    }

    private fun loadSnow(forecast4Day: ListItem?,city : String?) {
        loadImageFromAssets(SNOW,image!!)
        loadData(forecast4Day,city)
    }

    private fun loadClouds(forecast4Day: ListItem?,city : String?) {
        loadImageFromAssets(CLOUDS,image!!)
        loadData(forecast4Day,city)
    }

    private fun loadClear(forecast4Day: ListItem?,city : String?) {
        loadImageFromAssets(CLEAR,image!!)
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
        private val ARG_PARAM3 = "param3"

        const val THUNDER  = "thunder.jpg"
        const val DRIZZLE  = "drizzle.jpg"
        const val RAIN  = "rain.jpg"
        const val SNOW  = "snow.jpg"
        const val CLOUDS  = "clouds.jpg"
        const val CLEAR  = "clear.jpg"

        const val THUNDER_THMB  = "thumb/thunder_circle.png"
        const val DRIZZLE_THMB  = "thumb/drizzle_circle.png"
        const val RAIN_THMB  = "thumb/rain_circle.png"
        const val SNOW_THMB  = "thumb/snow_circle.png"
        const val CLOUDS_THMB  = "thumb/clouds_circle.png"
        const val CLEAR_THMB  = "thumb/clear_circle.png"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CompleteLoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: ListItem?, param2 : Array<ListItem?>?, param3: String?): ForecastDayFragment {
            val fragment = ForecastDayFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM1, param1)
            args.putSerializable(ARG_PARAM2, param2)
            args.putString(ARG_PARAM3, param3)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor

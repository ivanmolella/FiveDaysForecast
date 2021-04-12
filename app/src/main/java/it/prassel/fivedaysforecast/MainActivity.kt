package it.prassel.fivedaysforecast

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.tabs.TabLayout
import it.prassel.fivedaysforecast.model.ForecastResponse
import it.prassel.fivedaysforecast.model.ListItem
import it.prassel.fivedaysforecast.rest.HttpInvoker
import it.prassel.fivedaysforecast.util.AnimationUtil
import it.prassel.kotlin.androidfacilitylib.location.LocationFacility

import it.prassel.fivedaysforecast.model.Main
import it.prassel.fivedaysforecast.model.Wind

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.loading_overlay.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
        const val CITY_NAME = "London"

    }

    private var isAppStarted = false

    private var adapter : PagerAdapter? = null

    private var forecastResponse : ForecastResponse? = null
    private var fiveDay9AMWeather : List<ListItem?>? = null
    private var avgWeather4Day : MutableMap<Int,ListItem?>? = mutableMapOf()
    private var item4Day : MutableMap<Int,List<ListItem?>?>? = mutableMapOf()

    private var latitude : Double? = null
    private var longitude : Double? = null

    private var cityName : String? = null

    val mHandler : Handler = Handler()

    var ovl : View? = null

    var isOverlayActive : Boolean? = false

    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        internal var pageCount = 0


        override fun getItem(position: Int): Fragment {

            var frag: Fragment? = null


            frag = ForecastDayFragment.newInstance(avgWeather4Day!![position],item4Day!![position]?.toTypedArray(), cityName)

            return frag!!
        }


        override fun getCount(): Int {
            return pageCount
        }

        fun setPageCount(pageCount: Int) {
            this.pageCount = pageCount
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return "title"
        }
    }

    fun getDeniedPermission(context: Context): Array<String> {
        val deniedList = ArrayList<String>()
        try {
            val info = packageManager.getPackageInfo(this.packageName, PackageManager.GET_PERMISSIONS)
            if (info.requestedPermissions != null) {
                for (permission in info.requestedPermissions) {
                    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                        Log.d(TAG, "-- <MainActivity> permission $permission REQUESTED ")
                        deniedList.add(permission)
                    }
                }
            } else {
                Log.d(TAG, "Permission not required")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception (Ignored) = $e")
        }

        return deniedList.toTypedArray()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        val deniedList = ArrayList<String>()
        for (i in permissions.indices) {
            val permission = permissions[i]
            val result = grantResults[i]
            if (result == PackageManager.PERMISSION_DENIED) {
                android.util.Log.d(TAG, "$permission DENIED ")
                deniedList.add(permission)
            } else {
                android.util.Log.d(TAG, "$permission GRANTED ")
            }
        }

        if (deniedList.isEmpty()) {
            retrieveAddress()
        } else {

            MaterialDialog.Builder(this)
                .title("permission_mandatory")
                .content(deniedList.toString())
                .positiveText("OK")
                .cancelable(false)
                .show()
            //ToDO Sarebbe utilie che si desse la possibilità di chiedere nuovamente i permessi
            val builder = AlertDialog.Builder(this)
            builder.setTitle("permission_mandatory")
            builder.setMessage(deniedList.toString())
            builder.create().show()
        }
    }

    private fun startApplication() {
        this.isAppStarted=true
        loadWeatherData()
    }

    fun loadWeatherData(){
        //HttpInvoker.run(this,"https://api.openweathermap.org/data/2.5/forecast?q=${CITY_NAME}&appid=9f452c3f94f13e813b2666980bb093c5&units=metric") {
        val url = "https://api.openweathermap.org/data/2.5/forecast?lat=${latitude}&lon=${longitude}&appid=9f452c3f94f13e813b2666980bb093c5&units=metric"
        Log.i(TAG,"-- <MainActivity> url: $url")
        showOverlay()
        HttpInvoker.run(this,url) {
            this.forecastResponse=it
            if (this.forecastResponse != null){
                cityName = "${cityName}, ${forecastResponse?.city?.name} "
                this.fiveDay9AMWeather = this.forecastResponse?.list?.filter {
                    it?.dtTxt!!.contains("21:00:00")
                }
                if (this.fiveDay9AMWeather?.isNotEmpty() == true){
                    var i = 0
                    this.fiveDay9AMWeather?.forEach {
                        val day : String?= it?.dtTxt?.substring(0,10)
                        Log.i(TAG,"-- <MainActivity> day: $day")
                        var j = 0
                        val item4Day =
                            this.forecastResponse?.list?.filter {
                                val isDay = it?.dtTxt!!.contains(day!!,false)
                                if (isDay){
                                    j += 1
                                    var item = avgWeather4Day!![i]
                                    if (item == null){
                                        item = ListItem()
                                        item.main= Main()
                                        item?.main?.temp=0.0
                                        item?.main?.humidity=0
                                        item.weather= mutableListOf()
                                        item.wind= Wind()
                                        item?.wind?.speed=0.0
                                        avgWeather4Day!![i] = item
                                    }
                                    addDailyDataToItem(item,it)
                                }

                                isDay
                            }

                        avgWeather4Day!![i]?.main?.temp = avgWeather4Day!![i]?.main?.temp!! / j
                        avgWeather4Day!![i]?.main?.humidity = avgWeather4Day!![i]?.main?.humidity!! / j
                        avgWeather4Day!![i]?.wind?.speed = avgWeather4Day!![i]?.wind?.speed!! / j

                        Log.i(TAG,"-- <MainActivity> daily item temp: ${avgWeather4Day!![i]?.main?.temp} of elem $j")
                        Log.i(TAG,"-- <MainActivity> daily item humidity: ${avgWeather4Day!![i]?.main?.humidity}")
                        Log.i(TAG,"-- <MainActivity> daily item speed: ${avgWeather4Day!![i]?.wind?.speed}")
                        this.item4Day?.put(i++,item4Day)
                    }

                }
                runOnUiThread(){
                    hideOverlay()
                    initPager()
                }
            }else{
                runOnUiThread() {
                    Toast.makeText(
                        this,
                        "An Error has occurred retrieving weather informations...",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addDailyDataToItem(item: ListItem, it: ListItem) {

        it.weather?.forEach{
            item.weather?.add(it)
        }
        item?.main?.temp = item?.main?.temp?.plus(it?.main?.temp!!)
        item?.main?.humidity  = item?.main?.humidity?.plus(it?.main?.humidity!!)

        item?.wind?.speed  = item?.wind?.speed?.plus(it?.wind?.speed!!)

        //Log.i(TAG,"-- <MainActivity> addDailyDataToItem: $item")
    }

    private fun retrieveAddress() {

        avgWeather4Day?.clear()
        item4Day?.clear()

        LocationFacility.instance(this).getCurrentLocation(this, object  : LocationFacility.LocationEventListener{
            override fun onLocationFound(location: Location?) {
                this@MainActivity.latitude=location?.latitude
                this@MainActivity.longitude=location?.longitude


                Log.i(TAG,"-- <MainActivity> onLocationFound: Lat: ${location?.latitude} - Long ${location?.longitude} ")
                val address = LocationFacility.instance(this@MainActivity).resolveAddressFromCoordinate(this@MainActivity,location!!.latitude,location!!.longitude)
                val numAddressFound=address?.maxAddressLineIndex
                Log.i(TAG,"-- <MainActivity> onLocationFound: address $address ")
                Log.i(TAG,"-- <MainActivity> onLocationFound maxAddressLineIndex $numAddressFound ")
                if (numAddressFound != null && numAddressFound > -1){
                    val addressLine = address.getAddressLine(0)
                    cityName=address.locality
                    Log.i(TAG,"-- <MainActivity> onLocationFound city Name $cityName ")

                    //indirizzoSegnalazione?.text= Editable.Factory.getInstance().newEditable(addressLine)
                }

                if (cityName == null){
                    cityName="London"
                }

                startApplication()
            }

            override fun onUpdateLocation(location: List<Location>?) {
            }

            override fun onLocationNotFound(e: Exception?) {
                AnimationUtil.showGenericComunicationErrorSnackBar(activity_root,this@MainActivity)
            }

            override fun onBadSettingsForLocation() {
            }

        })
    }

    override fun onResume() {
        super.onResume()

        if (isAppStarted){
            retrieveAddress()
        }
    }


    fun showOverlay() {
        if (isOverlayActive == false) {
            Log.v(TAG, "-- <MainActivity> showOverlay")
            isOverlayActive = true
            AnimationUtil.showOverlay(loading_overlay, 200)
        }
    }

    fun showOverlay(captionText: String) {
        if (loading_overlay != null) {
            AnimationUtil.showOverlay(loading_overlay, AnimationUtil.OVERLAY_FADE_TIME)
        }
    }

    fun hideOverlay() {
        Log.v(TAG, "-- <MainActivity> hideOverlay")
        AnimationUtil.hideOverlay(loading_overlay, 200)
        isOverlayActive = false
    }


    private fun initPager() {

        adapter = PagerAdapter(supportFragmentManager)
        adapter!!.setPageCount(5)
        //mPager.setOffscreenPageLimit(3);

        pager.adapter = adapter

        tablayout.removeAllTabs()

        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![0])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![1])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![2])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![3])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![4])))

        //tablayout.tabGravity = TabLayout.GRAVITY_CENTER
        tablayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
        tablayout.setTabTextColors(Color.parseColor("#66FFFFFF"), Color.parseColor("#FFFFFF"))
        tablayout.setSelectedTabIndicatorColor(Color.WHITE)

        //Missed the tablayout bind... added Today
        tablayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(LayoutTab: TabLayout.Tab) {
                val delay = 100
                mHandler.postDelayed(
                    Runnable { pager.setCurrentItem(LayoutTab.position) },
                    delay.toLong()
                )
            }

            override fun onTabUnselected(LayoutTab: TabLayout.Tab) {}

            override fun onTabReselected(LayoutTab: TabLayout.Tab) {}
        })

        pager.offscreenPageLimit = 5
        pager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))


    }

    private fun getDay(listItem: ListItem?): String {
        val day = stringDateToCalendar("yyyy-MM-dd HH:mm:ss",listItem?.dtTxt!!)
        return day!!.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
    }


    fun stringDateToCalendar(srcDateFmt: String, srcDate: String): Calendar? {

        var cal: Calendar? = null

        val dt = SimpleDateFormat(srcDateFmt)
        try {
            val date = dt.parse(srcDate)
            cal = Calendar.getInstance()
            cal!!.time = date
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return cal
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setSupportActionBar(toolbar)
        if (isAppStarted == false) {
            val deniedPermission = getDeniedPermission(this)
            if (deniedPermission.size > 0) {
                ActivityCompat.requestPermissions(this, deniedPermission, 0)
            } else {
                retrieveAddress()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

package it.prassel.fivedaysforecast

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import it.prassel.fivedaysforecast.model.ForecastResponse
import it.prassel.fivedaysforecast.model.ListItem
import it.prassel.fivedaysforecast.rest.HttpInvoker

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val CITY_NAME = "London"
    }

    private var adapter : PagerAdapter? = null

    private var forecastResponse : ForecastResponse? = null
    private var fiveDay9AMWeather : List<ListItem?>? = null

    private inner class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        internal var pageCount = 0


        override fun getItem(position: Int): Fragment {

            var frag: Fragment? = null

            frag = ForecastDayFragment.newInstance(fiveDay9AMWeather!![position], CITY_NAME)

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

    override fun onResume() {
        super.onResume()

        HttpInvoker.run(this,"https://api.openweathermap.org/data/2.5/forecast?q=${CITY_NAME}&appid=9f452c3f94f13e813b2666980bb093c5&units=metric") {
            this.forecastResponse=it
            if (this.forecastResponse != null){
                this.fiveDay9AMWeather = this.forecastResponse?.list?.filter {
                    it?.dtTxt!!.contains("09:00:00")
                }
                runOnUiThread(){
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


    private fun initPager() {

        adapter = PagerAdapter(supportFragmentManager)
        adapter!!.setPageCount(5)
        //mPager.setOffscreenPageLimit(3);

        pager.adapter = adapter

        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![0])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![1])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![2])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![3])))
        tablayout!!.addTab(tablayout!!.newTab().setText(getDay(fiveDay9AMWeather!![4])))

        //tablayout.tabGravity = TabLayout.GRAVITY_CENTER
        tablayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary))
        tablayout.setTabTextColors(Color.parseColor("#66FFFFFF"), Color.parseColor("#FFFFFF"))
        tablayout.setSelectedTabIndicatorColor(Color.WHITE)

        pager.offscreenPageLimit = 5
        pager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablayout))

        this.fiveDay9AMWeather?.forEach {
            println("-- <WeatherTime> ${it?.dtTxt}")
        }



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

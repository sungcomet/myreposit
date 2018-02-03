package com.example.comet.drawertest

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.util.ArrayList
import java.util.HashMap

import java.sql.Types.NULL

class MainActivity : AppCompatActivity(){


    internal var arraylist = ArrayList<HashMap<String, String>>()
    internal var pages = ArrayList<String>()
    internal var lastnum = NULL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val Pagenum = findViewById<View>(R.id.albumpage) as TextView
        val Pagenumber = Pagenum.text.toString()
        supportActionBar!!.setTitle("우리학교앨범")


        try {
            lastnum = getlastnum().execute().get() / 10 + 1
        } catch (e: Exception) {

            e.printStackTrace()
        }

        for (i in 1..lastnum) {
            pages.add(Integer.toString(i))
        }


        doit().execute(Pagenumber)
        val yourListView = findViewById<View>(R.id.gridview) as GridView
        yourListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val url = arraylist[position][URL]
            val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(myIntent)
        }

        buttonforward.setOnClickListener {
            var Pagenumber = Pagenum.text.toString()

            if (Integer.parseInt(Pagenumber) < lastnum) {
                Pagenumber = Integer.toString(Integer.parseInt(Pagenumber) + 1)
                Pagenum.text = Pagenumber
                doit().execute(Pagenumber)
            }
        }
        buttonbackward.setOnClickListener {
            var Pagenumber = Pagenum.text.toString()

            if (Integer.parseInt(Pagenumber) > 1) {
                Pagenumber = Integer.toString(Integer.parseInt(Pagenumber) - 1)
                Pagenum.text = Pagenumber
                doit().execute(Pagenumber)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val buider = AlertDialog.Builder(this)//AlertDialog.Builder 객체 생성 
                val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, pages)
                buider.setTitle("페이지를 선택하세요")//Dialog 제목

                buider.setAdapter(adapter) { dialog, which ->
                    doit().execute(Integer.toString(which + 1),
                            "1")
                    albumpage.text = java.lang.String.format("%s", which + 1)
                }
                val a = buider.create()
                a.show()
            }
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    inner class doit : AsyncTask<String, Void, Void>() {

        val progressBar = progressbar
        private var progressBarStatus = 0

        override fun onPreExecute() {


            progressBar.setVisibility(View.VISIBLE)
            super.onPreExecute()

        }

        override fun doInBackground(vararg what: String): Void? {
            arraylist = ArrayList()

            try {
                val doc = Jsoup.connect("http://idong-p.gne.go.kr/index.jsp?mnu=M001006007&SCODE=S0000000294&frame=&search_field=&search_word=&category1=&category2=&category3=&search_year=&cmd=list&page=" + what[0] + "&nPage=1").get()
                val dates = doc.select("table")

                for (row in dates.select("td")) {
                    val links = row.select("a[href]")
                    val divs = row.select("div.image img[src]")
                    val map = HashMap<String, String>()
                    map.put("albumtitle", divs[0].attr("alt"))
                    map.put("picture", divs[0].attr("abs:src"))
                    map.put("url", links[0].attr("abs:href"))
                    arraylist.add(map)
                    progressBarStatus+=10
                    progressBar.progress = progressBarStatus

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            val adapter = GridViewAdapter(this@MainActivity, arraylist)
            // Pass the results into ListViewAdapter.java
            // Set the adapter to the ListView
            gridview.adapter = adapter
            progressBar.setVisibility(View.INVISIBLE)
        }
    }


    inner class getlastnum : AsyncTask<Void, Void, Int>() {

        override fun doInBackground(vararg what: Void): Int? {
            var strArr: Array<String>? = null
            try {
                val doc = Jsoup.connect("http://idong-p.gne.go.kr/index.jsp?SCODE=S0000000294&mnu=M001006007").get()
                val total = doc.select("dl")
                val tot = total.select("dd")[0]
                val texttest = tot.text()
                strArr = texttest.split("건".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return Integer.parseInt(strArr!![0])
        }

        override fun onPostExecute(aVoid: Int?) {
            super.onPostExecute(aVoid)


        }
    }

    companion object {

        internal var ALBUMTITLE = "albumtitle"
        internal var PICTURE = "picture"
        internal var URL = "url"
    }
}



internal class GridViewAdapter(// Declare Variables
        var context: Context,
        var data: ArrayList<HashMap<String, String>>) : BaseAdapter() {

    var resultp = HashMap<String, String>()

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Declare Variables
        val albumtitle: TextView
        val picture: ImageView

        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView = inflater.inflate(R.layout.albumlistview_item, parent, false)
        // Get the position
        resultp = data[position]


        // Locate the TextViews in listview_item.xml
        albumtitle = itemView.findViewById(R.id.albumtitle)
        picture = itemView.findViewById(R.id.picture)
        // Locate the ImageView in listview_item.xml


        // Capture position and set results to the TextViews
        albumtitle.text = resultp[MainActivity.ALBUMTITLE]
        albumtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.0f)
        Picasso.with(context).load(resultp[MainActivity.PICTURE]).memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .fit().into(picture)
        //new ImageLoaderTask(picture).execute(resultp.get(MainActivity.PICTURE));


        return itemView
    }
}
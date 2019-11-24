package com.ksc.imagesearch

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ksc.imagesearch.data.ImageSearchResponse
import com.ksc.imagesearch.repository.ItemRepository
import com.ksc.imagesearch.repository.NetworkState
import com.ksc.imagesearch.ui.ItemViewModel
import com.ksc.imagesearch.ui.ItemViewModelFactory
import com.ksc.imagesearch.ui.ItemsAdapter
import com.ksc.imagesearch.util.GlideApp
import com.ksc.imagesearch.util.KakaoApi
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var model :ItemViewModel
    val connectivityManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repo = ItemRepository(KakaoApi.create() ,Executors.newFixedThreadPool(5))
        model = ViewModelProviders.of(this, ItemViewModelFactory(repo)).get(ItemViewModel::class.java)

        initAdapter()
        initSwipeToRefresh()
        initEditText()
        initNetworkMonitor()
    }

    private fun initAdapter() {
        val adapter = ItemsAdapter(GlideApp.with(this)) {
            model.retry()
        }
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<ImageSearchResponse.Document>> {
            if (it.isNullOrEmpty()) {
                textView.visibility = View.VISIBLE
                textView.text = "No Results"
                checkNetwork()
            }
            adapter.submitList(it) {
                val layoutManager = (list.layoutManager as LinearLayoutManager)
                val position = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (position != RecyclerView.NO_POSITION) {
                    list.scrollToPosition(position)
                }
            }
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })

    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            if (editText.text.isNullOrBlank()) {
                swipe_refresh.isRefreshing = false
            } else {
                model.refresh()
            }
        }
    }

    private fun searchFromInput() {
        editText.text.toString().let {
            if (it.isNotBlank()) {
                if (model.searchImage(it)) {
                    list.scrollToPosition(0)
                    (list.adapter as? ItemsAdapter)?.submitList(null)
                }
            }
        }
    }

    private val searchHandler = Handler()

    private fun initEditText() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                swipe_refresh.visibility = View.VISIBLE
                textView.visibility = View.GONE
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) return
                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({this@MainActivity.searchFromInput()},1400)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        })
    }

    private fun checkNetwork() {
        val a = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (a == null || a == false) textView.text = "Internet Off"
    }

    private fun initNetworkMonitor() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        // first checking internet connection
        checkNetwork()

        connectivityManager.registerNetworkCallback(
            networkRequest,
            object :ConnectivityManager.NetworkCallback() {
                override fun onCapabilitiesChanged(
                    network: Network?,
                    networkCapabilities: NetworkCapabilities?
                ) {
                    setText(networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false)
                    Log.e("internet","${networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false}")
                }
                override fun onLost(network: Network?) {
                    super.onLost(network)
                    setText()
                }

                fun setText(boolean: Boolean = false) {
                    runOnUiThread {
                        if (boolean) {
                            textView.text = getString(R.string.welcome)
                        } else {
                            textView.text = "Internet Off"
                        }
                    }
                }
            }   // object :ConnectivityManager.NetworkCallback()
        )       // connectivityManager.registerNetworkCallback
    }           // initNetworkMonitor()

}
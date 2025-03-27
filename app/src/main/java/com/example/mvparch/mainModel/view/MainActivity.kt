package com.example.mvparch.mainModel.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvparch.common.SportEvent
import com.example.mvparch.databinding.ActivityMainBinding
import com.example.mvparch.mainModel.presenter.MainPresenter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var presenter: MainPresenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        presenter = MainPresenter(this)
        presenter.onCreate()
        setupAdapter()
        setupRecyclerView()
        setupSwipeRefresh()
        setupClicks()

    }

    private fun setupAdapter() {
        adapter = ResultAdapter(this)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.srlResults.setOnRefreshListener {
            //adapter.clear()
            //getEvents()
            //binding.btnAd.visibility = View.VISIBLE
            lifecycleScope.launch { presenter.refresh() }
        }
    }

    private fun setupClicks() {
        binding.btnAd.run {
            setOnClickListener {
                lifecycleScope.launch {
                    //binding.srlResults.isRefreshing = true
                    //  val events = getAdEventsInRealtime()
                    //EventBus.instance().publish(events.first())
                    lifecycleScope.launch { presenter.registerAd() }
                }
            }
            setOnLongClickListener { view ->
                lifecycleScope.launch {
                    //binding.srlResults.isRefreshing = true
                    //EventBus.instance().publish(SportEvent.ClosedAdEvent)
                    lifecycleScope.launch { presenter.closeAd() }

                }
                true
            }

        }
    }

//    private fun getEvents() {
//        lifecycleScope.launch {
//            val events = getResultEventsInRealtime()
//            events.forEach { event ->
//                delay(someTime())
//                EventBus.instance().publish(event)
//            }
//            lifecycleScope.launch { presenter.getEvents() }
//        }
//    }

    override fun onStart() {
        super.onStart()
        //binding.srlResults.isRefreshing = true
        //getEvents()
        lifecycleScope.launch { presenter.getEvents() }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    //OnClickListener interface
    override fun onClick(result: SportEvent.ResultSuccess) {
        //binding.srlResults.isRefreshing = true
        lifecycleScope.launch {
            //EventBus.instance().publish(SportEvent.SaveEvent)
            //SportService.instance().saveResult(result)
            presenter.saveResult(result)
        }
    }

    //View layer
    fun add(event: SportEvent.ResultSuccess) {
        adapter.add(event)
    }

    fun clearAdapter() {
        adapter.clear()
    }

    suspend fun showAdUI(isVisible: Boolean) = withContext(Dispatchers.Main){
        binding.btnAd.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    fun showProgress(isVisible: Boolean) {
        binding.srlResults.isRefreshing = isVisible
    }

    suspend fun showToast(msg: String) = withContext(Dispatchers.Main){
        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
    }

    fun showSnackbar(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}
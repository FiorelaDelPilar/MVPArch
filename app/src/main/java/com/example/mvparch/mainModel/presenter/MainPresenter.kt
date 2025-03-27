package com.example.mvparch.mainModel.presenter

import android.util.Log
import com.example.mvparch.common.EventBus
import com.example.mvparch.common.SportEvent
import com.example.mvparch.mainModel.model.MainRepository
import com.example.mvparch.mainModel.view.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainPresenter(private val view: MainActivity) {
    private val repository = MainRepository()
    private lateinit var viewScope: CoroutineScope

    fun onCreate() {
        viewScope = CoroutineScope(Dispatchers.IO + Job())
        onEvent()
    }

    suspend fun refresh() {
        view.clearAdapter()
        getEvents()
        view.showAdUI(true)
    }

    suspend fun getEvents() {
        view.showProgress(true)
        repository.getEvents()
    }

    suspend fun registerAd() {
        repository.registerAd()
    }

    suspend fun closeAd() {
        repository.closeAd()
    }

    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        //view.showProgress(true) => Curiosamente esto genera un punto blanco veloz al dar click a los items si es que se descomenta
        repository.saveResult(result)
    }

    fun onDestroy() {
        viewScope.cancel()
    }

    private fun onEvent() {
        viewScope.launch {
            EventBus.instance().subscribe<SportEvent> { event ->
                this.launch {
                    when (event) {
                        is SportEvent.ResultSuccess -> {
                            view.add(event)
                            view.showProgress(false)
                        }

                        is SportEvent.ResultError -> {
                            view.showSnackbar("Code ${event.code}, Message: ${event.msg} ")
                            view.showProgress(false)
                        }

                        is SportEvent.AdEvent -> {
                            view.showToast("Ad click. Send data to server---")
                        }

                        is SportEvent.ClosedAdEvent -> {
                            view.showAdUI(false)
                            Log.i("DEV:::", "Ad was closed. Send data to server")
                        }

                        is SportEvent.SaveEvent -> {
                            view.showToast("Guardado exitosamente")
                            view.showProgress(false)
                        }
                    }
                }
            }
        }
    }
}
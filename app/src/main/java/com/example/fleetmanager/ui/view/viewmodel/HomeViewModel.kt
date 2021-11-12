package com.example.fleetmanager.ui.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fleetmanager.data.repositories.FleetShipmentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: FleetShipmentsRepository) : ViewModel() {
    private val _myLiveData = MutableLiveData<List<String>>()
    private var repositoryDisposable: Disposable? = null

    // Observers will subscribe to this for notifications on drivers
    val driversLiveData: LiveData<List<String>>
        get() = _myLiveData

    // Observers will subscribe to this for notifications on the loading state
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    fun getDrivers() {
        loadingStateLiveData.postValue(LoadingState.LOADING)
        repositoryDisposable = repository.getDrivers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { drivers ->
                _myLiveData.postValue(drivers)
                loadingStateLiveData.postValue(LoadingState.COMPLETE)
            }
            .doOnError {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
            .subscribe({ }, { throwable -> println("Error getting shipments: ${throwable}")})
    }

    fun getAssignedShipment(driver: String): String {
        return repository.getAssignedShipment(driver)
    }

    override fun onCleared() {
        super.onCleared()

        repositoryDisposable?.dispose()
    }
}
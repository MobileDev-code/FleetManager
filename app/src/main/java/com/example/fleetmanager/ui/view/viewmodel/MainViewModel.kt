package com.example.fleetmanager.ui.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fleetmanager.data.repositories.FleetShipmentsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FleetShipmentsRepository) : ViewModel()  {
    private val _myLiveData = MutableLiveData<Map<String, String>>()
    private var repositoryDisposable: Disposable? = null

    // Observers will subscribe to this for notifications on shipment assignments
    val assignmentsLiveData: LiveData<Map<String, String>>
        get() = _myLiveData

    // Observers will subscribe to this for notifications on the loading state
    val loadingStateLiveData = MutableLiveData<LoadingState>()

    fun assignShipmentsToDrivers() {
        loadingStateLiveData.postValue(LoadingState.LOADING)
        repositoryDisposable = repository.assignShipmentsToDrivers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { assignedShipments ->
                _myLiveData.postValue(assignedShipments)
                loadingStateLiveData.postValue(LoadingState.COMPLETE)
            }
            .doOnError {
                loadingStateLiveData.postValue(LoadingState.ERROR)
            }
            .subscribe({ }, { throwable -> println("Error getting shipments: ${throwable}")})
    }

    override fun onCleared() {
        super.onCleared()

        repositoryDisposable?.dispose()
    }
}
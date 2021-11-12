package com.example.fleetmanager.data.repositories

import io.reactivex.Single

interface FleetShipmentsRepositoryContract {
    fun getShipments() : Single<List<String>>
    fun getDrivers() : Single<List<String>>
    fun getAssignedShipment(driver: String) : String
    fun assignShipmentsToDrivers(): Single<Map<String,String>>
}
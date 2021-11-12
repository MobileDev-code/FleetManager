package com.example.fleetmanager.data.repositories

import android.content.Context
import com.example.fleetmanager.AppApplication
import com.example.fleetmanager.data.model.ShipmentRouting
import com.google.gson.GsonBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FleetShipmentsRepository @Inject constructor(): FleetShipmentsRepositoryContract {
    lateinit var shipmentRouting: ShipmentRouting
    var assignedShipments: MutableMap<String, String> = mutableMapOf<String, String>()

    data class SuitabilityScore (
        val score: Double,
        val driver: String,
        val shipment: String
    )

    override fun getShipments(): Single<List<String>> {
        return Single.create {
            getShipmentRoutingData()
            it.onSuccess(shipmentRouting.shipments)
        }
    }

    override fun getDrivers(): Single<List<String>> {
        return Single.create {
            getShipmentRoutingData()
            it.onSuccess(shipmentRouting.drivers)
        }
    }

    override fun getAssignedShipment(driver: String): String {
        val shipment: String = assignedShipments[driver] ?: ""
        return shipment
    }

    override fun assignShipmentsToDrivers(): Single<Map<String,String>> {
        assignedShipments = mutableMapOf<String, String>()

        return Single.create {
            getDrivers().zipWith(getShipments())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { (drivers, shipments) ->
                    assignedShipments = getOptimalShipments(drivers, shipments)

                    it.onSuccess(assignedShipments)

                }
                .doOnError {
                }
                .subscribe({ }, { throwable -> println("Error getting shipments: ${throwable}")})
        }
    }

    fun getOptimalShipments(drivers: List<String>, shipments: List<String>): MutableMap<String, String> {
        val suitabilityScores: MutableList<SuitabilityScore> = mutableListOf<SuitabilityScore>()
        val assignedShipments = mutableMapOf<String, String>()
        val availableDrivers = drivers.toMutableList()

        for (shipment in shipments) {
            for (driver in availableDrivers) {
                val score = getSuitabilityScore(driver, shipment)
                val suitabilityScore = SuitabilityScore(score, driver, shipment)
                suitabilityScores.add(suitabilityScore)
            }
        }

        val orderedSuitabilityScores = suitabilityScores.sortedWith(compareBy ({ -it.score }, { it.driver }))
        for (orderedSuitabilityScore in orderedSuitabilityScores) {
            println(orderedSuitabilityScore)
            // Skip drivers and shipments that have been assigned
            if (!availableDrivers.contains(orderedSuitabilityScore.driver) || assignedShipments.containsValue(orderedSuitabilityScore.shipment)) {
                continue
            }
            assignedShipments[orderedSuitabilityScore.driver] = orderedSuitabilityScore.shipment
            availableDrivers.remove(orderedSuitabilityScore.driver)
        }

        for (assignedShipment in assignedShipments) {
            println(assignedShipment)
        }

        return assignedShipments
    }

    fun getSuitabilityScore(driver: String, shipmentDestination: String): Double {
        val vowels = arrayOf('a', 'e', 'i', 'o', 'u')
        val consonants = arrayOf('a','b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','y','z')

        val vowelsCount = driver.lowercase().filter { it in vowels }.length
        val consonantsCount = driver.lowercase().filter { it in consonants }.length
        val isEven = shipmentDestination.length % 2 == 0
        println("isEven ${isEven}")

        var suitabilityScore: Double = 0.0
        if (isEven) {
            suitabilityScore = vowelsCount.toDouble() * 1.5
        } else {
            suitabilityScore = consonantsCount.toDouble() * 1.0
        }

        val driversFactors = mutableListOf<Int>()
        for (i in 2..driver.length) {
            if (driver.length % i == 0) {
                driversFactors.add(i)
            }
        }
        val shipmentFactors = mutableListOf<Int>()
        for (i in 2..shipmentDestination.length) {
            if (shipmentDestination.length % i == 0) {
                shipmentFactors.add(i)
            }
        }

        val intersection = driversFactors intersect shipmentFactors
        if (intersection.size > 0) {
            suitabilityScore *= 1.5
        }

        println("vowelsCount ${vowelsCount}")
        println("consonantsCount ${consonantsCount}")

        return suitabilityScore
    }

    private fun getShipmentRoutingData(): ShipmentRouting {
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()
        val jsonFileName = "fleet_data.json"

        val json = readAsset(AppApplication.appContext, jsonFileName)
        shipmentRouting = gson.fromJson(json, ShipmentRouting::class.java)
        return shipmentRouting
    }

    private fun readAsset(context: Context, fileName: String): String =
        context
            .assets
            .open(fileName)
            .bufferedReader()
            .use(BufferedReader::readText)
}
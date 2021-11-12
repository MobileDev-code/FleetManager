package com.example.fleetmanager.data.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable
data class ShipmentRouting (
    val shipments: List<String>,
    val drivers: List<String>
)

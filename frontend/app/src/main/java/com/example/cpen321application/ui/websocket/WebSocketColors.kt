package com.example.cpen321application.ui.websocket

import androidx.compose.ui.graphics.Color

/**
 * spec: the websocket page palette. the page itself takes the home watercolour wash and its
 * ink, so only the walnut picture frame, its gold rivets and the two live states live here.
 */
internal object WebSocketColors {
    val WoodLit = Color(0xFF5A3720) // the grain where the light catches it, top-left
    val Wood = Color(0xFF3E2415) // deep espresso walnut
    val WoodDark = Color(0xFF241209)
    val WoodEdge = Color(0xFF1A0D05) // the outer bevel, where the frame turns away from the light
    val Rebate = Color(0xFF160B04) // the sunken lip the picture sits in
    val FrameShadow = Color(0xFF2B1A0E)
    val RivetLit = Color(0xFFFFE7A3) // the domed head of a rivet, lit from the top-left
    val Rivet = Color(0xFFD9A62B)
    val RivetShade = Color(0xFF80590F)
    val Connecting = Color(0xFFEAB308)
    val Connected = Color(0xFF22C55E)
}

package com.jdcoding.conteurifydimytaximeter

data class DirectionsResponse(
    val routes: List<Route>
)

data class Route(
    val overview_polyline: OverviewPolyline,
    val legs: List<Leg>
)

data class OverviewPolyline(
    val points: String
)

data class Leg(
    val distance: Distance,
    val duration: Duration
)

data class Distance(val text: String, val value: Int)
data class Duration(val text: String, val value: Int)


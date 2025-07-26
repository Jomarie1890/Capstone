package com.example.aquafence

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.maps.model.LatLng
import androidx.core.content.ContextCompat

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val CHANNEL_ID = "geofence_notifications"
    }

    // Fixed geofence coordinates for 3 areas: Sariaya, Lucena, Pagbilao
    val polyline1Points = listOf(
        LatLng(13.94586, 121.61155),  // LC point 1
        LatLng(13.94592, 121.61201),  // LC point 2
        LatLng(13.94567, 121.61207),  // LC point 3
        LatLng(13.94549, 121.61161),  // LC point 4
        LatLng(13.94586, 121.61155),  // LC point 5
        LatLng(13.758056, 121.627778),  // LC point 6
        LatLng(13.818333, 121.608333),  // LC point 7
        LatLng(13.837222, 121.597222),  // LC point 8
        LatLng(13.854444, 121.596667),  // LC point 9
        LatLng(13.864167, 121.595),      // LC point 10
        LatLng(13.884722, 121.583611),   // LC point 11
        LatLng(13.890556, 121.580278)    // LC point 12
    )

    val polyline2Points = listOf(
        LatLng(13.948889, 121.796667), // Pagbilao point 1
        LatLng(13.947222, 121.795833), // Pagbilao point 2
        LatLng(13.938611, 121.800556), // Pagbilao point 3
        LatLng(13.934167, 121.805),     // Pagbilao point 4
        LatLng(13.93, 121.796944),      // Pagbilao point 5
        LatLng(13.926111, 121.794722),  // Pagbilao point 6
        LatLng(13.923333, 121.793611),  // Pagbilao point 7
        LatLng(13.920556, 121.793056),  // Pagbilao point 8
        LatLng(13.918056, 121.793333),  // Pagbilao point 9
        LatLng(13.914167, 121.7875),    // Pagbilao point 10
        LatLng(13.912778, 121.785833),   // Pagbilao point 11
        LatLng(13.911111, 121.784722),   // Pagbilao point 12
        LatLng(13.910833, 121.784722),   // Pagbilao point 13
        LatLng(13.906667, 121.785278),   // Pagbilao point 14
        LatLng(13.902778, 121.784444),   // Pagbilao point 15
        LatLng(13.896111, 121.779722),   // Pagbilao point 16
        LatLng(13.892778, 121.776944),   // Pagbilao point 17
        LatLng(13.8875, 121.771389),   // Pagbilao point 18
        LatLng(13.882778, 121.765833),   // Pagbilao point 19
        LatLng(13.874444, 121.763333),   // Pagbilao point 20
        LatLng(13.866667, 121.761389),   // Pagbilao point 21
        LatLng(13.858611, 121.759444),   // Pagbilao point 22
        LatLng(13.8525, 121.757778),   // Pagbilao point 23
        LatLng(13.845, 121.755833),   // Pagbilao point 24
        LatLng(13.836667, 121.755556),   // Pagbilao point 25
        LatLng(13.738611, 121.75),   // Pagbilao point 26
        LatLng(13.7375, 121.7375),   // Pagbilao point 27
        LatLng(13.7375, 121.704444),   // Pagbilao point 28
        LatLng(13.743889, 121.674722),   // Pagbilao point 29
        LatLng(13.918333, 121.684444),   // Pagbilao point 30
        LatLng(13.923611, 121.690278),   // Pagbilao point 31
        LatLng(13.925833, 121.688611),   // Pagbilao point 32
    )

    val polyline3Points = listOf(
        LatLng(13.88102542, 121.5862307),
        LatLng(13.87292444, 121.5907189),
        LatLng(13.86482345, 121.5952071),
        LatLng(13.85596212, 121.597531),
        LatLng(13.84674736, 121.5982401),
        LatLng(13.83749098, 121.5985386),
        LatLng(13.82927348, 121.6024226),
        LatLng(13.82129131, 121.6071188),
        LatLng(13.81293642, 121.610985),
        LatLng(13.80412321, 121.6138305),
        LatLng(13.79531, 121.616676),
        LatLng(13.78649679, 121.6195215),
        LatLng(13.77768358, 121.6223671),
        LatLng(13.76887037, 121.6252126),
        LatLng(13.76005715, 121.6280581),
        LatLng(13.75431894, 121.6240032),
        LatLng(13.75054196, 121.6155471),
        LatLng(13.74676499, 121.6070911),
        LatLng(13.74455985, 121.5985763),
        LatLng(13.74769455, 121.5898618),
        LatLng(13.75082924, 121.5811472),
        LatLng(13.75396394, 121.5724327),
        LatLng(13.75709864, 121.5637182),
        LatLng(13.76023333, 121.5550036),
        LatLng(13.76658799, 121.5482667),
        LatLng(13.77294299, 121.5415299),
        LatLng(13.77921737, 121.534741),
        LatLng(13.78276813, 121.5261875),
        LatLng(13.78631888, 121.517634),
        LatLng(13.78986963, 121.5090806),
        LatLng(13.79342039, 121.5005271),
        LatLng(13.79697114, 121.4919736),
        LatLng(13.80052189, 121.4834202),
        LatLng(13.80407265, 121.4748667),
        LatLng(13.8076234, 121.4663132)
    )

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            Log.e("GeofenceReceiver", "Geofence event error: ${geofencingEvent?.errorCode}")
            return
        }

        val transition = geofencingEvent.geofenceTransition
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // Handle geofence transitions (ENTER or EXIT)
        if (!triggeringGeofences.isNullOrEmpty()) {
            when (transition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> handleGeofenceTransition(context, triggeringGeofences, "ENTERED")
                Geofence.GEOFENCE_TRANSITION_EXIT -> handleGeofenceTransition(context, triggeringGeofences, "EXITED")
                else -> Log.e("GeofenceReceiver", "Unknown geofence transition type")
            }
        }
    }

    private fun handleGeofenceTransition(context: Context, triggeringGeofences: List<Geofence>, action: String) {
        triggeringGeofences.forEach { geofence ->
            val message = "You $action the area of ${geofence.requestId}."
            showNotification(context, "Geofence Alert", message)
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        // Check for notification permission on Android 13 (SDK 33) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(CHANNEL_ID, "Geofence Alerts", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Channel for geofence entry/exit alerts"
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    // Helper function to check if the user is inside one of the defined geofence polygons
    private fun isLocationInsidePolygon(location: LatLng, polygonPoints: List<LatLng>): Boolean {
        var inside = false
        var j = polygonPoints.size - 1
        for (i in polygonPoints.indices) {
            val xi = polygonPoints[i].longitude
            val yi = polygonPoints[i].latitude
            val xj = polygonPoints[j].longitude
            val yj = polygonPoints[j].latitude
            val intersect = ((yi > location.latitude) != (yj > location.latitude)) &&
                    (location.longitude < (xj - xi) * (location.latitude - yi) / (yj - yi) + xi)
            if (intersect) inside = !inside
            j = i
        }
        return inside
    }
}

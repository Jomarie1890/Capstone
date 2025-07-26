package com.example.aquafence


import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions

class MapActivity : AppCompatActivity() {

    private val REQUEST_CODE_LOCATION_PERMISSION = 1
    private val NOTIFICATION_ID = 100
    private val CHANNEL_ID = "geofence_notifications"

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private val polygons = mutableListOf<Polygon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        createNotificationChannel(this)
        requestLocationPermission()

        mapView.getMapAsync { map ->
            googleMap = map
            googleMap.uiSettings.isZoomControlsEnabled = true
            setupGeofences()
            showUserLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun showUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation()
            } else {
                Toast.makeText(this, "Location permission is required to show your location.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val polygonAreaMap = mutableMapOf<Polygon, String>()

    private fun setupGeofences() {
        data class Area(val name: String, val coordinates: List<LatLng>)

        val areas = listOf(
            Area("Lucena", listOf(
                LatLng(13.925833, 121.688611), LatLng(13.923611, 121.690278), LatLng(13.918333, 121.684444),
                LatLng(13.743889, 121.674722), LatLng(13.758333, 121.633611), LatLng(13.758056, 121.627778),
                LatLng(13.818333, 121.608333), LatLng(13.837222, 121.597222), LatLng(13.854444, 121.596667),
                LatLng(13.864167, 121.595), LatLng(13.884722, 121.583611), LatLng(13.890556, 121.580278),
                LatLng(13.888875929839811, 121.5819197427708), LatLng(13.88957090282932, 121.58327349966277), LatLng(13.89007082837418, 121.5843034679625),
                LatLng(13.890487432170886, 121.58526906324354), LatLng(13.890737394089353, 121.58621320085165), LatLng(13.89107067622779, 121.58681401569318),
                LatLng(13.89144561806044, 121.58758649191799), LatLng(13.891653918816383, 121.58835896814281), LatLng(13.892497301491638, 121.59128232332758),
                LatLng(13.892939171894014, 121.59319410444664), LatLng(13.893439957331081, 121.59525761422597), LatLng(13.89415277932384, 121.59738398596411),
                LatLng(13.89434425557247, 121.59967569302752), LatLng(13.895086239830574, 121.60303137696792), LatLng(13.89570154204654, 121.60360930031324),
                LatLng(13.896606395277544, 121.60329237460707), LatLng(13.897438857126865, 121.60316187578715), LatLng(13.895948742364832, 121.60810488987904),
                LatLng(13.892431261534565, 121.61402497709216), LatLng(13.891028929515446, 121.61775427742847), LatLng(13.891353812731623, 121.61810333850235),
                LatLng(13.892624444932993, 121.61808188082948), LatLng(13.893343078092466, 121.61730940460464), LatLng(13.894766572917886, 121.61621095682293),
                LatLng(13.896067043097236, 121.61571336328561), LatLng(13.900606927788274, 121.61635954830612), LatLng(13.90347984816651, 121.61794642877659),
                LatLng(13.904281701723228, 121.6192123964594), LatLng(13.904521234297802, 121.6201136187217), LatLng(13.904665523387015, 121.62006730631089),
                LatLng(13.905304844073216, 121.6196507258892), LatLng(13.905432042525813, 121.62018736049797), LatLng(13.905565297972602, 121.62048687748889),
                LatLng(13.905880265087243, 121.62132302908854), LatLng(13.906305106505473, 121.62196045132497), LatLng(13.9071799122763, 121.62323718286322),
                LatLng(13.907763114286169, 121.62463193160245), LatLng(13.908023471851706, 121.62595157848656), LatLng(13.908096371917566, 121.62766819231948),
                LatLng(13.908347348091, 121.63087038068673), LatLng(13.908483580174082, 121.63400904761438), LatLng(13.907828373533997, 121.6388180919642),
                LatLng(13.908708807233587, 121.64774031991006), LatLng(13.909097169352263, 121.64852440732486), LatLng(13.910413551401982, 121.6511915032483),
                LatLng(13.912124836868287, 121.65299970387434), LatLng(13.913186150280792, 121.65658540053457), LatLng(13.914300750743323, 121.65895853894327),
                LatLng(13.91410259994257, 121.6611275364136), LatLng(13.9151378115524, 121.66292704340505), LatLng(13.915746336462393, 121.66455775673407),
                LatLng(13.914912151703204, 121.66560806060316), LatLng(13.912610048357449, 121.666200301221), LatLng(13.912099310138792, 121.66713436531438),
                LatLng(13.914038621104673, 121.66786695200814), LatLng(13.91351701566567, 121.66791210887214), LatLng(13.91379382383357, 121.66790591184518),
                LatLng(13.914340185065203, 121.66802037095354), LatLng(13.914276359201923, 121.6679948602678), LatLng(13.91458357244817, 121.66841328488958),
                LatLng(13.915039963021325, 121.66881812906303), LatLng(13.915136850830631, 121.66886455381122), LatLng(13.9157901256875, 121.66865084376634),
                LatLng(13.9163496972461, 121.66878381922963), LatLng(13.916748141749247, 121.66918078019472), LatLng(13.917449573569057, 121.67010385249993),
                LatLng(13.918090216301131, 121.67125699031779), LatLng(13.917864534152354, 121.671340131543), LatLng(13.917631734066, 121.6717176126295),
                LatLng(13.917465112179435, 121.67205020655962), LatLng(13.917715044964263, 121.67286559813027), LatLng(13.917840011255342, 121.67330548042497),
                LatLng(13.916999854684814, 121.67447449972758), LatLng(13.918287815717884, 121.67483174636976), LatLng(13.919872988678415, 121.67462760543138),
                LatLng(13.921037093149303, 121.6754186515676), LatLng(13.920690339238604, 121.67618418008561), LatLng(13.922770854897347, 121.67817455423486),
                LatLng(13.923216677244929, 121.67973112889003), LatLng(13.92381110570396, 121.681185633076), LatLng(13.92304330198973, 121.68174702065654),
                LatLng(13.923117605686446, 121.68215530253333), LatLng(13.92357578519482, 121.6831423467121), LatLng(13.923159240964061, 121.68417231501185),
                LatLng(13.922659386896187, 121.68417231501185), LatLng(13.92275310961626, 121.68451563777845), LatLng(13.92332585874646, 121.68488041821794),
                LatLng(13.923855170614267, 121.6851066596217), LatLng(13.924514124704382, 121.68508612532501), LatLng(13.924715529183873, 121.6856003702116),
                LatLng(13.925011925693052, 121.68658264661438), LatLng(13.924555389552717, 121.68678748030572), LatLng(13.923892674195782, 121.68701507329608),
                LatLng(13.923421408785792, 121.68706817832718), LatLng(13.92325941107917, 121.68729577131755), LatLng(13.92288387050437, 121.68753853717394),
                LatLng(13.922882368209654, 121.68816392763443), LatLng(13.92364258006652, 121.68839992816017), LatLng(13.924506906700383, 121.68831409746852),
                LatLng(13.924999446113512, 121.688556605671), LatLng(13.925462847833266, 121.68852441916161), LatLng(13.92565599371564, 121.68848769573799),
                LatLng(13.925751968234371, 121.68843347080126), LatLng(13.925928135382081, 121.68845555098795), LatLng(13.925920988552217, 121.68861702851638)
            )),
            Area("Pagbilao", listOf(
                LatLng(13.948889, 121.796667), LatLng(13.947222, 121.795833), LatLng(13.938611, 121.800556),
                LatLng(13.934167, 121.805), LatLng(13.93, 121.796944), LatLng(13.926111, 121.794722),
                LatLng(13.923333, 121.793611), LatLng(13.920556, 121.793056), LatLng(13.918056, 121.793333),
                LatLng(13.914167, 121.7875), LatLng(13.912778, 121.785833), LatLng(13.911111, 121.784722),
                LatLng(13.906667, 121.785278), LatLng(13.902778, 121.784444), LatLng(13.896111, 121.779722),
                LatLng(13.892778, 121.776944), LatLng(13.8875, 121.771389), LatLng(13.882778, 121.765833),
                LatLng(13.874444, 121.763333), LatLng(13.866667, 121.761389), LatLng(13.858611, 121.759444),
                LatLng(13.8525, 121.757778), LatLng(13.845, 121.755833), LatLng(13.836667, 121.755556),
                LatLng(13.738611, 121.75), LatLng(13.7375, 121.7375), LatLng(13.7375, 121.704444),
                LatLng(13.743889, 121.674722), LatLng(13.918333, 121.684444), LatLng(13.923611, 121.690278),
                LatLng(13.925833, 121.688611), LatLng(13.928594127456911, 121.69196393234756), LatLng(13.929714965146788, 121.69817095834705),
                LatLng(13.932623584070612, 121.69859453927253), LatLng(13.934861364559755, 121.69981451670387), LatLng(13.938351478265812, 121.69785063698632),
                LatLng(13.952898908044894, 121.6923186031342), LatLng(13.960791468781897, 121.69523179341085), LatLng(13.960202481042426, 121.70530657645098),
                LatLng(13.964914340809525, 121.70846253258405), LatLng(13.969626104218106, 121.71453167899375), LatLng(13.968448172401208, 121.7244850791057),
                LatLng(13.968802061331212, 121.73331775543689), LatLng(13.966643018970945, 121.74253489365259), LatLng(13.968185194150353, 121.751434199516),
                LatLng(13.962633315191646, 121.75683734950451), LatLng(13.956155954017044, 121.76510099066341), LatLng(13.945811068276686, 121.77215806049148),
                LatLng(13.944462854181731, 121.77497617210773), LatLng(13.942880125266347, 121.77926770669006), LatLng(13.940080832800849, 121.78337320757633),
                LatLng(13.939680121211566, 121.78667744114568), LatLng(13.941496291578785, 121.78772188054114), LatLng(13.9440304589056, 121.78746077069228),
                LatLng(13.944662926322401, 121.7867766982299), LatLng(13.945310866178728, 121.78656017406712), LatLng(13.946098898686998, 121.78713757183452),
                LatLng(13.945677598513399, 121.7883973824256), LatLng(13.945927934253703, 121.78862497541596), LatLng(13.946678939844917, 121.78879187694223),
                LatLng(13.947793598093421, 121.78894425633631), LatLng(13.948194016749495, 121.78951552613825), LatLng(13.948625236063357, 121.79043590526359),
                LatLng(13.94809441585916, 121.79138243931615),  LatLng(13.948147015941283, 121.79246640233931),  LatLng(13.948287282768261, 121.79360456351363),
                LatLng(13.948203986606526, 121.79401228954403), LatLng(13.947829136816166, 121.79434488347415), LatLng(13.94758964913123, 121.79490278296986),
                LatLng(13.947672886570398, 121.79547142209077), LatLng(13.947912374076292, 121.79579325885234), LatLng(13.948151861426215, 121.79590054721687),
                LatLng(13.948485060803808, 121.7961365816189), LatLng(13.948734235092166, 121.79635275508124), LatLng(13.94899450801705, 121.79643862911732),
                LatLng(13.948889, 121.796667)
            )),
            Area("Sariaya", listOf(
                LatLng(13.888933776899837, 121.58205628882197),
                LatLng(13.888712837984352, 121.58216249887496),
                LatLng(13.888550815979109, 121.58228388179263),
                LatLng(13.888374064571451, 121.58239009184562),
                LatLng(13.888197313028991, 121.5825114747633),
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
                LatLng(13.8076234, 121.4663132),
                LatLng(13.81629981463602, 121.4617734888596),
                LatLng(13.816508182778948, 121.4615589121491),
                LatLng(13.81671655073552, 121.46134433543854),
                LatLng( 13.816883244966597, 121.46112975872802),
                LatLng(13.817029102320951, 121.46095809735958),
                LatLng(13.817195796328397, 121.460722062978),
                LatLng(13.817406054748238, 121.46039727676194),
                LatLng(13.817756485026282, 121.45996422847391),
                LatLng(13.818584968443561, 121.46007612821849),
                LatLng(13.818961679064728, 121.46033776158181),
                LatLng(13.81932774313354, 121.46047723047195),
                LatLng(13.8198122932054, 121.46058191558426),
                LatLng(13.820489778847989, 121.46080160873153),
                LatLng(13.821079121872668, 121.4610292017219),
                LatLng(13.821727397477343, 121.46131748617638),
                LatLng(13.82326879637536, 121.46196544909475),
                LatLng(13.82439394846667, 121.46290958670285),
                LatLng(13.826810923462844, 121.46415413173173),
                LatLng(13.838214401090568, 121.47291277778352),
                LatLng(13.843783305668417, 121.48032839746666),
                LatLng(13.845011459837544, 121.48210687236852),
                LatLng(13.846052059675632, 121.48407172890045),
                LatLng(13.847702382240755, 121.48620824681569),
                LatLng(13.853205338999272, 121.49474590086811),
                LatLng(13.855728036983292, 121.50037552910022),
                LatLng(13.857830264375693, 121.5090364956111),
                LatLng(13.86063320463278, 121.51668701602908),
                LatLng(13.861614225729868, 121.52390448812153),
                LatLng(13.864136832374731, 121.52924541746994),
                LatLng(13.866379126380242, 121.53646288956234),
                LatLng(13.869586944316055, 121.54454172584526),
                LatLng(13.870644365049095, 121.54811757647465),
                LatLng(13.87164603116526, 121.55194113871279),
                LatLng(13.872058480662048, 121.55461156313305),
                LatLng(13.874823113608029, 121.55814555119474),
                LatLng(13.87678503169013, 121.56059949170616),
                LatLng(13.88055588767118, 121.56634063707266),
                LatLng(13.882811252758792, 121.57028546273727),
                LatLng(13.885521491247854, 121.5738055676549),
                LatLng(13.886434716643965, 121.57650633780723),
                LatLng(13.886776871567616, 121.57718537181619),
                LatLng(13.886953930789671, 121.57760379643796),
                LatLng(13.887245557448743, 121.57822606895239),
                LatLng(13.887536388219425, 121.57893618207741),
                LatLng(13.887765522906541, 121.57949408157312),
                LatLng(13.887994657367088, 121.58013781176045),
                LatLng(13.88828628271647, 121.58072789776553),
                LatLng(13.888484174951703, 121.58111413186981),
                LatLng(13.888713320280523, 121.58152178954265),
                LatLng(13.88887996286542, 121.58192948532798),
                LatLng(13.889262170745162, 121.5821687400599)
            ))
        )

        areas.forEachIndexed { index, area ->
            val polygon = googleMap.addPolygon(
                PolygonOptions()
                    .addAll(area.coordinates)
                    .strokeColor(
                        when (index) {
                            0 -> 0xFF00FF00.toInt() // Green
                            1 -> 0xFFFF0000.toInt() // Red
                            else -> 0xFFFFFF00.toInt() // Yellow
                        }
                    )
                    .fillColor(
                        when (index) {
                            0 -> 0x5000FF00.toInt() // Semi-transparent green
                            1 -> 0x50FF0000.toInt() // Semi-transparent red
                            else -> 0x50FFFF00.toInt() // Semi-transparent yellow
                        }
                    )
                    .strokeWidth(2f) // Thinner boundaries
            )
            polygons.add(polygon)
            polygonAreaMap[polygon] = area.name

        }
    }

    private fun setupLocationTracking() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.let { result ->
                    for (location in result.locations) {
                        if (location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            checkUserInsidePolygon(latLng)
                        }
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        }
    }

    private fun checkUserInsidePolygon(userLocation: LatLng) {
        var enteredArea: String? = null

        polygonAreaMap.forEach { (polygon, areaName) ->
            if (isLocationInsidePolygon(userLocation, polygon.points)) {
                enteredArea = areaName
                return@forEach
            }
        }

        if (enteredArea != null) {
            showNotification("Geofence Triggered", "You have entered $enteredArea!")
        } else {
            showNotification("Geofence Triggered", "You have exited all geofenced areas!")
        }
    }

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

    private fun createNotification(context: Context, title: String, message: String): Notification {
        // Ensure notification channel is created for Android Oreo and above
        createNotificationChannel(context)

        // Build the notification
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon)  // Ensure this is a small icon (e.g., 24x24 dp)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.warning))  // Decode using context
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))  // BigTextStyle for long messages
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String, message: String) {
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, createNotification(this, title, message))
    }

    private fun playCustomAlarm() {
        // Play a custom alarm sound once
        val alarmUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE + R.raw.custom_alarm)
        val ringtone = RingtoneManager.getRingtone(this, alarmUri)
        ringtone.play()
    }

    @SuppressLint("WrongConstant")
    private fun createNotificationChannel(context: Context) {
        // Create a notification channel if targeting Android Oreo (API level 26) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Geofence Notifications"
            val descriptionText = "Notifications for entering and exiting geofences"
            val importance = NotificationManagerCompat.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManager.createNotificationChannel(channel)
        }
    }
}


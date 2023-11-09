package com.example.menutest

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.androidplot.util.PixelUtils
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import com.example.menutest.databinding.ActivityMainBinding
import com.example.menutest.ui.home.HomeViewModel
import com.example.menutest.ui.settings.SettingsViewModel
import com.example.menutest.ui.settings.Settingsfragment
import com.google.android.material.navigation.NavigationView
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private var deviceName: String = ""
    private var myDeviceNames: String = ""
    private var deviceHardwareAddress: String = "" //MAC address
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_ENABLE_BT = 0xface
    private lateinit var plot: XYPlot
    var chargePercent: Float = 0.0F
    var powerOutput: Float = 0.0F
    private lateinit var homeViewModel: HomeViewModel
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var settingsViewModel: SettingsViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PixelUtils.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setSupportActionBar(binding.appBarMain.toolbar)

        // Home View Model Stuff
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Example: Set the data when it changes (you can set it from your data source)
        homeViewModel.setData1("")
        homeViewModel.setData2("")

        // View model for settings fragment
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        //BT Setup running as background thread
        val thread = Thread(Runnable {
            BTsetup()
        })
        thread.start()

//        // if the initial background thread connection fails check the button on the settings page
//        val connectButton = findViewById<Button>(R.id.connectButton)
//        connectButton.setOnClickListener {
//            // Call the BTsetup function to initiate the connection
//            BTsetup()
//        }

//        val thread2 = Thread(Runnable {
//           doPlot()
//        })
//        thread2.start()

        //========================================================

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    // Set up BT
    @RequiresApi(Build.VERSION_CODES.M)
    fun BTsetup() {
        val myTextView = findViewById<TextView>(R.id.myTextView)
        val textView2 = findViewById<TextView>(R.id.textView2)

        //get the BT adapter from Android API
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth (function returns, thread dies)
        } else {
//            myTextView.text = "BTA NOT NULL"
            //if phone has BT turned on
            if (bluetoothAdapter?.isEnabled == true) {
                //check for BT permissions for app, if not then request permission
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    val MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 0xba55
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT
                    )
                }
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)


//                if (ContextCompat.checkSelfPermission(
//                        this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    val MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 0xba55
//                    myTextView.text = "BT permission NOT granted"
//                    ActivityCompat.requestPermissions(this,
//                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
//                        MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT)
//                    myTextView.text = "BT request perm done"
//                }
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                myTextView.text = "BT adapter NOT enabled"
            }
            //if
            if (bluetoothAdapter.bondedDevices == null) {
                myTextView.text = "BTA.bonded null"
            } else {
                myTextView.text = "BTA.bonded NOT null"
                //get Set of BT devices and get ESP32
                val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

                pairedDevices?.forEach { device ->
//                    myDeviceNames += device.name + "\n"
                    //find ESP32 device
                    if (device.name == "ESP32-BT-Slave") {
                        deviceName = device.name
                        deviceHardwareAddress = device.address // MAC address
                    }
                }
//                myTextView.text = "Battery Charge Level: "
//                myTextView.text = "chosen: " + deviceName +"\n others:\n" + myDeviceNames
                var isToastShown = false
                while (true) {

                    try {
                        //get ESP32 device and create socket and connect to it
                        val device: BluetoothDevice =
                            bluetoothAdapter.getRemoteDevice(deviceHardwareAddress)
                        //create socket using common serial port UUID
                        val socket =
                            device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                        socket.connect()
                        //get BT serial data from ESP32 and parse it, then update the textViews on home page
                        val inputStream: InputStream =
                            socket.inputStream //interface with socket object char stream
                        val outputStream: OutputStream = socket.outputStream
                        val buffer = ByteArray(1024)
                        var bytes: Int
                        while (true) {
                            bytes = inputStream.read(buffer) //return number of available bytes
                            val rxString =
                                String(buffer, 0, bytes) //convert buffer of bytes to a string
                            val parts =
                                rxString.split(" ") // split the string into an array of substrings based on the space delimiter
                            try {
                                chargePercent =
                                    parts[0].toFloat() //first part is always charge percent
//                            myTextView.text = String.format("%5.1f %%", chargePercent)
                                // Format chargePercent and update LiveData1 in HomeViewModel
                                val formattedChargePercent =
                                    String.format("%5.1f %%", chargePercent)
                                mainHandler.post {
                                    homeViewModel.setData1(formattedChargePercent)
                                }
                            } catch (e: java.lang.NumberFormatException) {
                                mainHandler.post {
//                                myTextView.text = "-----.-----"
                                    homeViewModel.setData1("-----.-----")
                                }
                            }
                            try {
                                powerOutput = parts[1].toFloat() //second part is always power value
//                          textView2.text = String.format("%5.3f W", powerOutput)
                                val formattedPowerOutput = String.format("%5.3f W", powerOutput)
                                mainHandler.post {
                                    homeViewModel.setData2(formattedPowerOutput)
                                }

                            } catch (e: java.lang.NumberFormatException) {
                                mainHandler.post {
//                                textView2.text = "-----.-----"
                                    homeViewModel.setData2("-----.-----")
                                }
                            }

//                             writing to the ESP32
//                            val dataToSend = 5.5f // Float value to send
                            val dataToSend = settingsViewModel.desiredChargeLevel.value
//                            Log.d("BTsetup", "desiredChargeLevel in BTsetup: $dataToSend")

//                            Log.d("SETTINGS", "Text from EditText: $textFromEditText")
                            val byteBuffer = dataToSend?.let {
                                ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                                    .putFloat(it)
                            }
                            val dataBytes = byteBuffer?.array()

                            if (dataBytes != null) {
                                outputStream.write(dataBytes)
                            } else {
//                                Log.e("BTSetup", "dataBytes is null or empty")
                            }
                        }
                    } catch (e: IOException) {
                        if (!isToastShown) {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Bluetooth Connection Failed",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                            isToastShown = true
                        }
                    }
                }
            }
        }
    }
}
//    fun doPlot() {
//        val plot = findViewById<XYPlot>(R.id.plotCharge)
//        val chargedArray = arrayOf<Number>(4.5, 7.5, 9.0, 7.5)
//        val chargedSeries = SimpleXYSeries(
//            chargedArray.asList(),
//            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
//            "Charge vs Time"
//        )
//        val chargedFormat = LineAndPointFormatter(Color.RED, Color.GREEN, null, null)
//        try {
//            plot.addSeries(chargedSeries, chargedFormat)
//        }
//        catch (e: java.lang.NullPointerException){
//            return
//        }
////        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
////            override fun format(
////                obj: Any?,
////                toAppendTo: StringBuffer?,
////                pos: FieldPosition?
////            ): StringBuffer {
////                val i = (obj as Number).toFloat().roundToInt()
////                if (toAppendTo != null) {
////                    return toAppendTo.append("")
////                }
////                return StringBuffer()
////            }
////
////            override fun parseObject(p0: String?, p1: ParsePosition?): Any? {
////                return null
////
////            }
////        }
//    }
//    fun batteryUpdate(
//        ChargeLevel: Int)
//    {
//        val myImageView: ImageView = findViewById(R.id.baseline_battery)

//    if(ChargeLevel = 0){
//        battery0.setVisibility(View.VISIBLE);
//    }
//    else{
//        battery0.setVisibility(View.GONE);
//    }
//    }
//}
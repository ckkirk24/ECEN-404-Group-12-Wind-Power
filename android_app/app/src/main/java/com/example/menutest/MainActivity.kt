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
import android.util.Log
import android.view.Menu
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
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
    private  var deviceName:String = ""
    private  var myDeviceNames:String = ""
    private  var deviceHardwareAddress:String = "" //MAC address
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_ENABLE_BT = 0xface
    private lateinit var plot: XYPlot
    var chargePercent: Float = 0.0F
    var powerOutput: Float = 0.0F
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PixelUtils.init(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)



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
    fun BTsetup(){
        val myTextView = findViewById<TextView>(R.id.myTextView)
        val textView2 = findViewById<TextView>(R.id.textView2)

        val fragmentManager = supportFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.nav_settings)
        // Step 4: Access the EditText within the fragment
        val editTextInFragment = fragment?.view?.findViewById<EditText>(R.id.edit_text)

        // Step 5: Get the text from the EditText
        val textFromEditText = editTextInFragment?.text.toString()

//        val desiredCharge = findViewById<EditText>(R.id.edit_text)
        Log.d("SETTINGS", "Text from EditText: $textFromEditText")
//        val myVariableText = "Hello, world!"
//        myTextView.text = myVariableText
        //get the BT adapter from Android API
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth (function returns, thread dies)
        }
        else {
//            myTextView.text = "BTA NOT NULL"
            //if phone has BT turned on
            if (bluetoothAdapter?.isEnabled == true) {
                //check for BT permissions for app, if not then request permission
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    val MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 0xba55
                    ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                        MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT)
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
            }
            else {
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
                try{
                    //get ESP32 device and create socket and connect to it
                    val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceHardwareAddress)
                    //create socket using common serial port UUID
                    val socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
                    socket.connect()
                    //get BT serial data from ESP32 and parse it, then update the textViews on home page
                    val inputStream: InputStream = socket.inputStream //interface with socket object char stream
                    val outputStream: OutputStream = socket.outputStream
                    val buffer = ByteArray(1024)
                    var bytes: Int
                    while (true) {
                        bytes = inputStream.read(buffer) //return number of available bytes
                        val rxString = String(buffer, 0, bytes) //convert buffer of bytes to a string
                        val parts = rxString.split(" ") // split the string into an array of substrings based on the space delimiter
                        try {
                            chargePercent = parts[0].toFloat() //first part is always charge percent
                            myTextView.text = String.format("%5.1f %%", chargePercent)
                        }
                        catch (e: java.lang.NumberFormatException){
                            myTextView.text = "-----.-----"
                        }
                        try {
                              powerOutput = parts[1].toFloat() //second part is always power value
                            runOnUiThread {
                                textView2.text = String.format("%5.3f W", powerOutput)
                            }
                        }
                        catch (e: java.lang.NumberFormatException){
                            runOnUiThread {
                                textView2.text = "-----.-----"
                            }
                        }

                        // writing to the ESP32
                        val dataToSend = 5.5f // Float value to send
//                        val dataToSend = desiredCharge.text.toString().toFloat()
                        val byteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(dataToSend)
                        val dataBytes = byteBuffer.array()



                        outputStream.write(dataBytes) // Send data to ESP32 over Bluetooth


                    }
                }
                catch (e: IOException){
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "Failed to establish Bluetooth connection",
                            Toast.LENGTH_SHORT
                        ).show()
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
    fun batteryUpdate(
        ChargeLevel: Int)
    {
//        val myImageView: ImageView = findViewById(R.id.baseline_battery)

//    if(ChargeLevel = 0){
//        battery0.setVisibility(View.VISIBLE);
//    }
//    else{
//        battery0.setVisibility(View.GONE);
//    }
    }
}


#include "BluetoothSerial.h"

//#define USE_PIN // Uncomment this to use PIN during pairing. The pin is specified on the line below
const char *pin = "1234"; // Change this to more secure PIN.

String device_name = "ESP32-BT-Slave";

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Bluetooth not available or not enabled. It is only available for the ESP32 chip.
#endif

BluetoothSerial SerialBT;





void setup() {
  Serial.begin(115200);
  SerialBT.begin(device_name); //Bluetooth device name
  Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
  #ifdef USE_PIN
    SerialBT.setPin(pin);
    Serial.println("Using PIN");
  #endif
}

void loop() {

//to add
//current sensor readings, power calculations, power data sent


  //voltage sensor for generator
  float genVolt = analogRead(34);
  //multiply by 2.72727272727 + offset value
  double genVoltage = (((genVolt/4096) * 3.3 * 2.72727272727)+0.4);
  //voltage sensor for battery
  float batVolt = analogRead(35);
//multiply by 11 + offset value
  double batVoltage = (((batVolt/4096) * 3.3* 11.0062893082)+1.6);
  //current sensor
  //float AcsValue = analogRead(32);
  float baseVal = 2.28;
  //average of current input
  float ACSValue = 0.0, Samples = 0.0, AvgACS = 0.0, batSamples = 0.0, genSamples = 0.0, AvgGen = 0.0, AvgBat = 0.0, batVal = 0.0, genVal = 0.0; //Change BaseVol as per your reading in the first step.
  for (int x = 0; x < 1500; x++) { //This would take 500 Samples
    ACSValue = analogRead(32);
    batVal = analogRead(35);
    genVal = analogRead(34);
    genVal = (((genVal/4096) * 3.3 * 2.72727272727)+0.4);
    batVal = (((batVal/4096) * 3.3* 11.0062893082)+1.6);
    genSamples = genSamples + genVal;
    Samples = Samples + ACSValue;
    batSamples = batSamples + batVal;
    delay (3);
  }
  AvgACS = Samples/1500;
  AvgGen = genSamples/1500;
  AvgBat = batSamples/1500;
  Serial.println("Current");
  Serial.println(((((AvgACS) * (3.3 / 4095.0))-baseVal  ) / 0.066)-.15 ); //0.066V = 66mVol. This is sensitivity of your ACS module.
  Serial.println("Generator Voltage");
  Serial.println(AvgGen);

  Serial.println("Battery Voltage");
  Serial.println(AvgBat);


 // Serial.println(batVoltage);
//Serial.println(genVoltage);
//adjusted current sensor readings
  float AcsValueF = (((((AvgACS) * (3.3 / 4095.0))-baseVal  ) / 0.066)-.15 );
//battery percentage calculation
  float batVoltagePercent = (AvgBat / 8.4) * 100;
//charge control
  if(batVoltagePercent <90) {
    analogWrite(27,0);
  }
  else {
    analogWrite(27,255);
  }


//wattage calculation
  float Wattage = AcsValueF * AvgGen;
//wattage, battery percent
  SerialBT.println(Wattage);
  SerialBT.println(batVoltagePercent);
 

  /*Serial.println("raw");
  Serial.println(AcsValue);
  Serial.println("formula");
  Serial.println(AcsValueF);*/

  delay(200);
}

//This example code is in the Public Domain (or CC0 licensed, at your option.)
//By Evandro Copercini - 2018
//
//This example creates a bridge between Serial and Classical Bluetooth (SPP)
//and also demonstrate that SerialBT have the same functionalities of a normal Serial

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

// Sine wave generation using PWM
const int sampleRate = 1;  // Sample rate in Hz
const float frequency = 0.1;  // Frequency of the sine wave in Hz
const float amplitude = 9.0;  // Amplitude of the sine wave (0-255)
const float offset = 4.5;  // Offset of the sine wave (0-255)



void setup() {
  Serial.begin(115200);
  SerialBT.begin(device_name); //Bluetooth device name
  Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
  //Serial.printf("The device with name \"%s\" and MAC address %s is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str(), SerialBT.getMacString()); // Use this after the MAC method is implemented
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
  double genVoltage = (genVolt * 2.73) / 4096;
  //voltage sensor for battery
  float batVolt = analogRead(35);
  double batVoltage = (batVolt * 7.27) / 4096;
  //current sensor
  float AcsValue = analogRead(32);
  float AcsValueF = (2.5 - (AcsValue * (5.0 / 4096)) )/0.066;
  //charge control
  float batVoltagePercent = (batVoltage / 8.4) * 100;
  if(batVoltagePercent >=90) {
    analogWrite(32,0);
  }
  else {
    analogWrite(32,255);
  }

  if (Serial.available()) {
    SerialBT.write(Serial.read());
  }
  if (SerialBT.available()) {
    Serial.write(SerialBT.read());
  }
  //SerialBT.println("Bat Voltage, Gen Voltage");
  //SerialBT.println(batVoltage);
 // Serial.println(AcsValueF);

  //delay(200);

  float time = (float)millis() / 1000.0;  // Current time in seconds
  float value = (amplitude/2) * sin(2.0 * PI * frequency * time) + offset;  // Sine wave value (-1.0 to 1.0)
  float percentage = (value/9.0) * 100.0;  // Convert value to a percentage out of 9

// Print the percentage value
  Serial.print("Percentage: ");
  Serial.println(percentage);
  
  // Wait for the next sample time
  Serial.print(time);
  Serial.print(" ");
  Serial.println(value);
  
  //SerialBT.println(value);
  
  SerialBT.print(percentage);
  
  delay(1000 / sampleRate);
}

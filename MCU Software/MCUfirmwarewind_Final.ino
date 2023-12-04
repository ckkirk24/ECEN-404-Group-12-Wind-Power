

#include "BluetoothSerial.h"

//#define USE_PIN // Uncomment this to use PIN during pairing. The pin is specified on the line below
const char *pin = "1234"; // Change this to more secure PIN.

String device_name = "WindPowerDevice";

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Bluetooth not available or not enabled. It is only available for the ESP32 chip.
#endif

BluetoothSerial SerialBT;





void setup() {
  
//Bluetooth connection
  Serial.begin(115200);
  SerialBT.begin(device_name); //Bluetooth device name
  Serial.printf("The device with name \"%s\" is started.\nNow you can pair it with Bluetooth!\n", device_name.c_str());
  #ifdef USE_PIN
    SerialBT.setPin(pin);
    Serial.println("Using PIN");
  #endif

}
unsigned long previousMillis = 0;  // will store last time LED was updated

// constants won't change:
const long intervalOn = 2000;  // interval at which to blink (milliseconds)
const long intervalOff = 2000;
void loop() {
  float batVoltagePercent;
  pinMode(32, OUTPUT); // set the pin as output

//lookup table for generator and battery offset values

double genValues[] = {.983,.9877,.9923,.997,1.0016,1.0063,1.0109,1.0156,1.0202,1.0295,1.03415,1.0388,1.04345,1.0481,
1.05275,1.0574,1.06205,1.0667,1.07135,1.076,1.08065,1.0853,1.08995,1.0946,1.09925,1.1039,1.10855,1.1132,1.11785,1.1225,1.12715,
1.1318,1.13645,1.1411,1.14575,1.1504,1.15505};

double batValues[] = {.381,.38555,.3901,.39465,.3992,.40375,.4083,.41285,.4174,.4265,.43105,.4356,.44015,.4447,.44925,
.4538,.45835,.4629,.46745,.472,.47655,.4811,.48565,.4902,.49475,.4903,.50385,.5084,.51295,.5175,.52205,.382,.22675,.0715,-.08375,
-.239,-.39425};


double thresholds[37];
for (int j = 0; j <= 36; j++) {
        thresholds[j] = j * 0.25;
    }
int n = sizeof(thresholds) / sizeof(thresholds[0]);

//   //voltage sensor for generator
//   //multiply by 2.72727272727 + offset value
//   //voltage sensor for battery


  float baseVal = 2.54;
  //500 sample rate of sensor readings
  float ACSValue = 0.0, Samples = 0.0, AvgACS = 0.0, batSamples = 0.0, genSamples = 0.0, AvgGen = 0.0, AvgBat = 0.0, batVal = 0.0, genVal = 0.0; //Change BaseVol as per your reading in the first step.
  for (int x = 0; x < 500; x++) { //This would take 1500 Samples
    ACSValue = analogRead(33);
    genVal = analogRead(35);
    genVal = (((genVal*3.3)/4095) * 7.70547945205);
    AvgACS = ((((ACSValue*3.3)/ 4095)-baseVal)/.185);// /0.066
    for (int j = 0; j < n; j++) {
        if (genVal <= thresholds[j]) {
            genVal += genValues[j];
            break; // Exit loop once a condition is met
        }
    }
    //add to sameples
    genSamples = genSamples + genVal;
    Samples = Samples + AvgACS;
    delay (1);
  }
  //average out samples
  if(AvgACS < 0)
  {
    AvgACS = 0;
  }
  AvgACS = (Samples/500)+.01;
  AvgGen = genSamples/500;

  float receivedFloat;
  // Reading in data from app
  if (SerialBT.available()) {
  // Read the incoming bytes into a buffer
  byte buffer[4]; // Floats are 4 bytes long
  SerialBT.readBytes(buffer, sizeof(buffer));
  memcpy(&receivedFloat, buffer, sizeof(receivedFloat));
  // Print the received float
  Serial.printf("Max Battery Charge: %5.1f %%\n", receivedFloat);
  }
//battery percentage calculation

unsigned long currentMillis = millis();

if (currentMillis - previousMillis >= intervalOn) {
  //code to stop charge, take percentage reading, start charge
// save the last time you blinked the LED
  previousMillis = currentMillis;
  if(digitalRead(32) == LOW)
  { 
    digitalWrite(32, HIGH);
    for (int x = 0; x < 500; x++) { //This would take 1500 Samples

      batVal = analogRead(34);
      batVal = (((batVal/4095) * 3.3 * 2.89575289575));
      for (int i = 0; i < n; i++) {
          if (batVal <= thresholds[i]) {
              batVal += batValues[i];
              break; // Exit loop once a condition is met
          }
      }
  //add to sameples
    batSamples = batSamples + batVal;  
    }
  }
  AvgBat = (batSamples/500);
  float Wattage = (AvgGen * AvgACS);
  batVoltagePercent = int((AvgBat / 8.4) * 100);
  char buf1[10];
  char buf2[10];
  dtostrf(batVoltagePercent,5, 2, buf1); // convert the float percentage to a string with 2 decimal places
  dtostrf(Wattage, 5, 2, buf2); // convert the float value2 to a string with 2 decimal places
  String s1 = String(buf1);
  String s2 = String(buf2);
  s1.trim();
  s2.trim();
  String s = s1 + " " + s2; // concatenate the strings
  SerialBT.print(s); // print the concatenated string to the SerialBT object
  }
  else
  {
    digitalWrite(32, LOW);
  }



//charge control
if(batVoltagePercent >= receivedFloat) {
  digitalWrite(32,HIGH);
}



  delay(200);
}

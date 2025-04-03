/*
  This sketch demonstrates how to create a Bluetooth® Low Energy (BLE) peripheral
  with the Arduino Uno R4 Wifi  board. It creates a service with two characteristics
  that can be used to control an espresso machine.
*/

#include <ArduinoBLE.h>
BLEService newService("180A");  // creating the service

BLEByteCharacteristic espressoChar("00002AC1-0000-1000-8000-00805F9B34FB", BLERead | BLEWrite);  // creating the LED characteristic
BLEByteCharacteristic lungoChar("00002AC2-0000-1000-8000-00805F9B34FB", BLERead | BLEWrite);     // creating the LED characteristic


const int espressonPin = 5;
const int lungoPin = 7;
long previousMillis = 0;


void setup() {
  Serial.begin(9600);  // initialize serial communication
  while (!Serial)
    ;  //starts the program if we open the serial monitor.

  pinMode(LED_BUILTIN, OUTPUT);
  pinMode(espressonPin, OUTPUT);
  pinMode(lungoPin, OUTPUT);

  //initialize ArduinoBLE library
  if (!BLE.begin()) {
    Serial.println("starting Bluetooth® Low Energy failed!");
    while (1)
      ;
  }

  BLE.setLocalName("TeoArduino");  //Setting a name that will appear when scanning for Bluetooth® devices
  BLE.setAdvertisedService(newService);

  newService.addCharacteristic(espressoChar);  //add characteristics to a service
  newService.addCharacteristic(lungoChar);     //add characteristics to a service

  BLE.addService(newService);  // adding the service

  espressoChar.writeValue(0);  //set initial value for characteristics
  lungoChar.writeValue(0);

  BLE.advertise();  //start advertising the service
  Serial.println(" Bluetooth® device active, waiting for connections...");
}

void loop() {

  BLEDevice central = BLE.central();  // wait for a Bluetooth® Low Energy central

  if (central) {  // if a central is connected to the peripheral
    Serial.print("Connected to central: ");

    Serial.println(central.address());  // print the central's BT address

    digitalWrite(LED_BUILTIN, HIGH);  // turn on the LED to indicate the connection

    // check the battery level every 200ms
    // while the central is connected:
    while (central.connected()) {
      long currentMillis = millis();

      if (currentMillis - previousMillis >= 200) {  // if 200ms have passed, we check the battery level
        previousMillis = currentMillis;


        if (espressoChar.written() && espressoChar.value()) {
          Serial.println("Espresso button triggered");
          digitalWrite(espressonPin, HIGH);  // Press button
          delay(1200);                  // Hold for 300ms
          digitalWrite(espressonPin, LOW);   // Release button
          espressoChar.writeValue(0);  // Reset the characteristic so it can be triggered again
        }

        if (lungoChar.written() && lungoChar.value()) {
          Serial.println("Lungo button triggered");
          digitalWrite(lungoPin, HIGH);
          delay(1200);
          digitalWrite(lungoPin, LOW);
          lungoChar.writeValue(0);
        }
      }
    }

    digitalWrite(LED_BUILTIN, LOW);  // when the central disconnects, turn off the LED
    Serial.print("Disconnected from central: ");
    Serial.println(central.address());
  }
}
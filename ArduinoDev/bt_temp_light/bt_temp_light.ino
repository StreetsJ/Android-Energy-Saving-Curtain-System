#include <dht11.h>
#include <SoftwareSerial.h>

//#include <dht.h>
#define dht_apin A1 
#define ledPin 7
dht11 DHT;
SoftwareSerial BTserial(0, 1); // RX | TX

//Define the variable that contains the led
int state = 0;

void setup(){
  //Setting the pin mode and initial LOW
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, LOW);
  Serial.begin(9600);
  delay(500);//Delay to let system boot
  Serial.println("DHT11 Humidity & temperature Sensor, Light Intensity Sensor, BT Communication Module\n\n");
  delay(1000);//Wait before accessing Sensor
 
}//end "setup()"
 
void loop(){
  //Start of Program 
  // Checks if the data is coming from the serial port
  if(Serial.available() > 0){
    state = Serial.read(); // Read the data from the serial port
  }
    //Deciding functions for LED on and off
  if (state == '0') {
    digitalWrite(ledPin, LOW); // Turn LED OFF
    // Send back, to the phone, the String "LED: ON"
    Serial.println("LED: OFF");
    state = 0;
  }
  else if (state == '1') {
    digitalWrite(ledPin, HIGH);
    Serial.println("LED: ON");;
    state = 0;
  }
 
    DHT.read(dht_apin);
    Serial.print("Current humidity = ");
    Serial.print(DHT.humidity);
    Serial.print("%  ");
    Serial.print("temperature = ");
    Serial.print(DHT.temperature); 
    Serial.println("C  ");

    // Light intensity
    int lightIntensity = analogRead(A0);
    Serial.print("Light intensity reading = ");
    Serial.print(lightIntensity);   // the raw analog reading
    // We'll have a few threshholds, qualitatively determined
    if (lightIntensity < 100) {
      Serial.println(" - Dark");
    } else if (lightIntensity < 200) {
      Serial.println(" - Light");
    } else if (lightIntensity < 500) {
      Serial.println(" - Bright");
    } else if (lightIntensity < 800) {
      Serial.println(" - Very Bright");
    } else {
      Serial.println(" - Dark");
    }

    Serial.println();

    BTserial.print(DHT.humidity);
    BTserial.print(",");
    BTserial.print(DHT.temperature);
    BTserial.print(",");
    BTserial.print(lightIntensity);



    
    delay(1000);//Wait 1 second before accessing sensor again.
    //exit(0);
 
}// end loop(

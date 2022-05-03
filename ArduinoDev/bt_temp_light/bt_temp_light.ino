#include <dht_nonblocking.h>
#include <dht11.h>

//www.elegoo.com
//2018.10.25

#include <SoftwareSerial.h>
#include <dht_nonblocking.h>
#define DHT_SENSOR_TYPE DHT_TYPE_11
static const int DHT_SENSOR_PIN = 7;
DHT_nonblocking dht_sensor( DHT_SENSOR_PIN, DHT_SENSOR_TYPE );

#define dht_window_apin A1
#define dht_curr_apin A0 

const int RX_PIN = 2;
const int TX_PIN = 3;
SoftwareSerial serial(RX_PIN, TX_PIN);
char commandChar;
int state;
dht11 DHTwindow;
dht11 DHTcurr;


/*
 * Initialize the serial port.
 */
void setup( )
{
  serial.begin(9600);
  Serial.begin(38400);
}


/*
 * Poll for a measurement, keeping the state machine alive.  Returns
 * true if a measurement is available.
 */
static bool measure_environment( float *temperature, float *humidity )
{
  static unsigned long measurement_timestamp = millis( );

  /* Measure once every four seconds. == 4000ul */
//  if( millis( ) - measurement_timestamp > 300000ul )
  if (millis( ) - measurement_timestamp > 10000ul )
  {
    if( dht_sensor.measure( temperature, humidity ) == true )
    {
      measurement_timestamp = millis( );
      return( true );
    }
  }

  return( false );
}


/*
 * Main program loop.
 */
void loop( )
{
  float temperature;
  float humidity;

  /* Measure temperature and humidity.  If the functions returns
     true, then a measurement is available. */
  if (state)
  {
    DHTwindow.read(dht_window_apin);
    serial.print(DHTwindow.temperature * 9/5 + 32);
    serial.print("W");
    DHTcurr.read(dht_curr_apin);
    serial.print(DHTcurr.temperature * 9/5 + 32);
    serial.print("C#");
    delay(5000);
  }
  
  if (serial.available())
  {
    commandChar = serial.read();
    switch(commandChar)
    {
      case '*':
        DHTwindow.read(dht_window_apin);
        serial.print(DHTwindow.temperature * 9/5 + 32);
        serial.print("W");
        DHTwindow.read(dht_curr_apin);
        serial.print(DHTcurr.temperature * 9/5 + 32);
        serial.print("C#");
        break;
      case '0':
        state = 0;
        break;
      case '1':
        state = 1;
        break;
    }
  }
}

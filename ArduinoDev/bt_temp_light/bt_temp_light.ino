#include <dht_nonblocking.h>

//www.elegoo.com
//2018.10.25

#include <SoftwareSerial.h>
#include <dht_nonblocking.h>
#define DHT_SENSOR_TYPE DHT_TYPE_11
static const int DHT_SENSOR_PIN = 7;
DHT_nonblocking dht_sensor( DHT_SENSOR_PIN, DHT_SENSOR_TYPE );

const int RX_PIN = 2;
const int TX_PIN = 3;
SoftwareSerial serial(RX_PIN, TX_PIN);
char commandChar;
int state;

/*
 * Initialize the serial port.
 */
void setup( )
{
  serial.begin(9600);
//  Serial.begin(38400);
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
    if( measure_environment( &temperature, &humidity ) == true )
    {
      serial.print(temperature * 9/5 + 32);
      serial.print("#");
    } 
  }
  
  if (serial.available())
  {
    commandChar = serial.read();
    switch(commandChar)
    {
      case '*':
        serial.print(temperature * 9/5 + 32, 1);
        serial.print("#");
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

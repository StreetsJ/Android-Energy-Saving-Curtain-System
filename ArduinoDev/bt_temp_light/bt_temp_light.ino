#include <dht11.h>
#include <SoftwareSerial.h>

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
}

/*
 * Main program loop.
 */
void loop( )
{
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

#include <WiFi.h>

const char* ssid = "lux-meter";
const char* password =  "qazwsxedc735";
int lastState = HIGH; // the previous state from the input pin
double delta;
bool isCalibrated;

WiFiServer wifiServer(80);

void setup() {

    Serial.begin(115200);

    delay(1000);
    WiFi.softAP(ssid, password);
   
    wifiServer.begin();
    pinMode(36, INPUT);
    pinMode(14, INPUT_PULLUP);
}

double luxRead() {
    double lux[3];
    double luxAverage = 0;
    for (int i = 0; i < 3; i++) {
        lux[i] = (analogRead(36) * 0.9765625) - delta;
        delay(3);
        luxAverage += lux[i];
    }
    return luxAverage / 3;
}

void calibrate(WiFiClient *client) {
    while (client->connected()) {
        while (client->available() > 0) {
            char c = client->read();
            if (c == 'o') {
                delta = luxRead();
                isCalibrated = true;
                client->write('o');
                delay(10);
                client->write('\n');
                delay(10);
                return;
            } else {
                double lux = luxRead();
                String luxStr = String(lux) + '\n';
                for (int i = 0; i < luxStr.length(); i++) {
                    char luxChar = luxStr.charAt(i);
                    client->write(luxChar);
                    delay(10);
                }
                Serial.println(analogRead(36));
            }
        }
    }
}

void loop() {

  WiFiClient client = wifiServer.available();

  if (client) {

    while (client.connected()) {

      while (client.available()>0) {
            delay(200);
            char c = client.read();
            if (c == 'h' && !isCalibrated) {
                client.write('c');
                delay(10);
                client.write('\n');
                delay(10);
                calibrate(&client);
            } else {
                double lux = luxRead();
                String luxStr = String(lux) + '\n';
                for (int i = 0; i < luxStr.length(); i++) {
                    char luxChar = luxStr.charAt(i);
                    client.write(luxChar);
                    delay(10);
                }
                Serial.println(analogRead(36));
            }
      }

      delay(10);
    }

    client.stop();
    Serial.println("Client disconnected");

  }
}

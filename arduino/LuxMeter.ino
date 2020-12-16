#include <WiFi.h>

const char* ssid = "lux-meter";
const char* password =  "qazwsxedc735";
int lastState = HIGH; // the previous state from the input pin
int currentState;

WiFiServer wifiServer(80);

void setup() {

    Serial.begin(115200);

    delay(1000);
    WiFi.softAP(ssid, password);
    wifiServer.begin();
    pinMode(36, INPUT);
    pinMode(24, INPUT_PULLDOWN);
}

double luxRead() {
    double lux[3];
    double luxAverage = 0;
    for (int i = 0; i < 3; i++) {
        lux[i] = analogRead(36) * 0.9765625;
        delay(3);
        luxAverage += lux[i];
    }
    return luxAverage / 3;
}

void loop() {

  WiFiClient client = wifiServer.available();

  if (client) {

    while (client.connected()) {

      while (client.available()>0) {
            delay(200);
            currentState = digitalRead(14);
            if(currentState == LOW) {
                Serial.println("The state changed from LOW to HIGH");
                // save the the last state
                lastState = currentState;
                client.write('$');
                delay(10);
                client.write('\n');
                delay(450);
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

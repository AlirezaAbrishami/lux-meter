#include <WiFi.h>
#include <pthread.h>

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

void *socketReaderThread(void *client) {
    WiFiClient* c = (WiFiClient*)client;
    bool done = false;
    Serial.println("Thread!");
    while (!done) {
        yield();
        delay(10);
        if (c->read() == 'o') {
            isCalibrated = true;
            c->write('o');
            delay(10);
            c->write('\n');
            delay(10);
            Serial.println("OOOOOOOOOOOOOOOOOOOOOOOOOOO");
            done = true;
        }
    }
    Serial.println("DONE");
}

void calibrate(WiFiClient *client) {
    pthread_t thread;
    int returnValue = pthread_create(&thread, NULL, socketReaderThread, (void*)client);
 
    if (returnValue) {
        Serial.println("An error has occurred");
    }
    Serial.println("calibrate");
    while (client->connected()) {
            delay(200);
            if (isCalibrated) {
                delta = luxRead();
                return;
            } else {
                double lux = luxRead();
                String luxStr = "hi: " + String(lux) + '\n';
                for (int i = 0; i < luxStr.length(); i++) {
                    char luxChar = luxStr.charAt(i);
                    client->write(luxChar);
                    delay(10);
                }
                Serial.println(analogRead(36));
        }
    }
}

void loop() {

  WiFiClient client = wifiServer.available();

  if (client) {

    while (client.connected()) {

      while (client.available()>0) {
            delay(200);
            if (!isCalibrated) {
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


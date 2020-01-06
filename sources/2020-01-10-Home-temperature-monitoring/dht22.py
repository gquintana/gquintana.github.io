import Adafruit_DHT
from time import sleep
from datetime import datetime
from influxdb import InfluxDBClient
import logging
import logging.config
import yaml


def read_config():
    with open('/home/pi/dht22.yml') as config_file:
        return yaml.load(config_file)


class DHT22Reader:
    def __init__(self, pin):
        self.pin = pin
        self.logger = logging.getLogger('DHT22Reader')
        self.logger.info('Using DHT22 on pin {}'.format(str(pin)))

    def read(self):
        try:
            humidity, temperature = Adafruit_DHT.read_retry(Adafruit_DHT.DHT22,
                                                            self.pin)
            if humidity and temperature:
                self.logger.info("Temp={0:0.1f}*C Humidity={1:0.1f}%"
                                 .format(temperature, humidity))
                return (self.pin, humidity, temperature)
            else:
                self.logger.info("No data")
                return None
        except RuntimeError as e:
            self.logger.warn("RuntimeError: " + str(e))
            return None


class InfluxDBWriter:
    def __init__(self, database):
        self.client = InfluxDBClient(database=database)
        self.logger = logging.getLogger('InfluxDBWriter')
        self.logger.info('Using InfluxDB database {}'.format(database))

    def write(self, pin, humidity, temperature):
        time = datetime.utcnow().isoformat()
        influxdb_point = {"measurement": "dht22",
                          "tags": {"pin": pin,
                                   "host": "raspberrypi"},
                          "time": time,
                          "fields": {"humidity": humidity,
                                     "temperature": temperature}}
        self.client.write_points([influxdb_point])

    def close(self):
        self.client.close()


def main():
    config = read_config()
    print(str(config))
    logging.config.dictConfig(config['logging'])
    app_config = config['application']
    reader = DHT22Reader(int(app_config['pin']))
    writer = InfluxDBWriter(app_config['database'])
    period = int(app_config['period'])

    try:
        while True:
            data = reader.read()
            if data:
                pin, humidity, temperature = data
                writer.write(pin, humidity, temperature)
            sleep(period)
    except KeyboardInterrupt:
        writer.close()


if __name__ == '__main__':
    main()

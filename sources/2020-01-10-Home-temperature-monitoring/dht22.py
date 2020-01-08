import Adafruit_DHT
from time import sleep, time
from datetime import datetime
from influxdb import InfluxDBClient
import logging
import logging.config
import yaml
import traceback


def read_config():
    with open('/home/pi/dht22.yml') as config_file:
        return yaml.load(config_file)


class DHT22Reader:
    def __init__(self, pin):
        self.sensor = Adafruit_DHT.DHT22
        self.pin = pin
        self.logger = logging.getLogger('DHT22Reader')
        self.logger.info('Using DHT22 on pin {}'.format(str(pin)))

    def read(self):
        try:
            humidity, temperature = Adafruit_DHT.read_retry(self.sensor, self.pin)
            if humidity and (humidity < 0 or humidity > 100):
                self.logger.warn("Invalid Humidity={0:0.1f}%".format(humidity))
                humidity = None

            if temperature and (temperature < -50 or temperature > 100):
                self.logger.warn("Invalid Temp={0:0.1f}%".format(temperature))
                temperature = None

            if humidity or temperature:
                self.logger.info("Temp={0:0.1f}*C Humidity={1:0.1f}%".format(temperature, humidity))
                return (self.pin, humidity, temperature)
            else:
                self.logger.info("No data")
                return None
        except RuntimeError as e:
            self.logger.error("RuntimeError:" + str(e) + traceback.format_exc())
            return None


class InfluxDBWriter:
    def __init__(self, database):
        self.client = InfluxDBClient(database=database)
        self.logger = logging.getLogger('InfluxDBWriter')
        self.logger.info('Using InfluxDB database {}'.format(database))

    def write(self, pin, humidity, temperature):
        time = datetime.utcnow().isoformat()
        influxdb_point = {"measurement": "dht22",
                          "tags": {"pin": pin, "host": "raspberrypi"},
                          "time": time,
                          "fields": {
                            "humidity": humidity,
                            "temperature": temperature}}
        self.client.write_points([influxdb_point])

    def close(self):
        self.client.close()


def main():
    config = read_config()
    logging.config.dictConfig(config['logging'])
    logging.info("DHT22 starting")
    app_config = config['application']
    # DHT22 sensor connected to GPIO12.
    reader = DHT22Reader(int(app_config['pin']))
    writer = InfluxDBWriter(app_config['database'])
    period = int(app_config['period'])

    try:
        while True:
            start_time = time()
            data = reader.read()
            if data:
                pin, humidity, temperature = data
                writer.write(pin, humidity, temperature)
            end_time = time()
            duration = end_time - start_time
            if (duration > period):
                logging.warn("Took {}s to read/write".format(duration))
            else:
                sleep(period - duration)
    except Exception as e:
        logging.error("Exception:" + str(e) + traceback.format_exc())
    finally:
        writer.close()


if __name__ == '__main__':
    main()

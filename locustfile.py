import time
from binascii import hexlify

from locust import HttpUser, task, between
import random
import base64

seed = time.time()
rnd = random.Random(seed)
print(f'using seed {seed}')

instances = [hexlify(rnd.randbytes(8)).decode() for x in range(160)]
reasons = [hexlify(rnd.randbytes(256)).decode() for x in range(160)]
modules = [hexlify(rnd.randbytes(4)).decode() for x in range(120)]


def ip_address():
    return f'{rnd.randint(0, 255)}.{rnd.randint(0, 255)}.{rnd.randint(0, 255)}.{rnd.randint(0, 255)}'


class EventsUser(HttpUser):
    wait_time = between(0, 3)

    @task
    def get_instance_events(self):
        instance_id = rnd.choice(instances)
        response = self.client.get(f'/api/events/instance', params={'instance_id': instance_id})
        print(f'found {len(response.json())} events for instance {instance_id}')

    @task
    def connect_event(self):
        self.client.post('/api/event/connect', json={
            'instanceId': rnd.choice(instances),
            'ipAddress': ip_address(),
            'success': rnd.randint(0, 2) % 2 == 1
        }, headers={'Content-Type': 'application/json'})

    @task
    def data_event(self):
        data_length = rnd.randint(100, 10240)
        data = base64.b64encode(rnd.randbytes(data_length)).decode()
        self.client.post('/api/event/data', json={
            'instanceId': rnd.choice(instances),
            'sha1': hexlify(rnd.randbytes(32)).decode(),
            'data': data,
            'length': data_length
        }, headers={'Content-Type': 'application/json'})

    @task
    def analytics_event(self):
        self.client.post('/api/event/analytics', json={
            'instanceId': rnd.choice(instances),
            'uptime': rnd.randint(10000, 100000000000),
            'ramAvailable': rnd.randint(100000,10000000000),
            'cpuLoad': rnd.randint(0, 400)
        }, headers={'Content-Type': 'application/json'})

    @task
    def crash_event(self):
        self.client.post('/api/event/crash', json={
            'instanceId': rnd.choice(instances),
            'stackTrace': base64.b64encode(rnd.randbytes(256)).decode(),
            'module': rnd.choice(modules),
        }, headers={'Content-Type': 'application/json'})

    @task
    def disconnect_event(self):
        self.client.post('/api/event/disconnect', json={
            'instanceId': rnd.choice(instances),
            'reason': rnd.choice(reasons)
        }, headers={'Content-Type': 'application/json'})

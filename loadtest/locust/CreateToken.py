from locust import HttpLocust, TaskSet, task, constant
import random

class MultiUserTaskSet(TaskSet):

    @task
    def loadtest(self):
        user = str(random.randint(0, 100000000) % 3000)
        response1 = self.client.post("/pay/" + user)
        if response1.status_code != 200:
            print (response1)

class LoadTest(HttpLocust):
    task_set = MultiUserTaskSet
    wait_time = constant(0)

from locust import HttpLocust, TaskSet, task, constant
import random

class MultiUserTaskSet(TaskSet):

    @task
    def loadtest(self):
        user = str(random.randint(0, 100000000) % 3000)
        response1 = self.client.post("/pay/" + user)
        if response1.status_code != 200:
            print (response1)
        token = response1.json()["tokenId"]


        response2 = self.client.request(method="POST", url="/pay/" + user + "/" + token + "/1/1")
#        print (response2.json())

class LoadTest(HttpLocust):
    task_set = MultiUserTaskSet
    wait_time = constant(0)

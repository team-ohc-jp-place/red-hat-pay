from locust import HttpLocust, TaskSet, task, constant
from random

class MultiUserTaskSet(TaskSet):

    @task
    def loadtest(self):
        user = str(random.randint(0, 99))
        response1 = self.client.post("/pay/" + user)
        token = response1.json()["tokenId"]
#        print (token)

        response2 = self.client.request(method="POST", url="/pay/" + user + "/" + token + "/1/1")
#        print (response2.json())

class LoadTest(HttpLocust):
    task_set = MultiUserTaskSet
    wait_time = constant(0)

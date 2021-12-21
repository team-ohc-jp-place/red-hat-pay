from locust import HttpLocust, TaskSet, task, constant
from random

class SingleUserTaskSet(TaskSet):

    @task
    def loadtest(self):
        user = str(0)
        response1 = self.client.post("/pay/" + user)
        token = response1.json()["tokenId"]
#        print (token)

        response2 = self.client.request(method="POST", url="/pay/" + user + "/" + token + "/1/1")
#        print (response2.json())

class LoadTest(HttpLocust):
    task_set = SingleUserTaskSet
    wait_time = constant(0)

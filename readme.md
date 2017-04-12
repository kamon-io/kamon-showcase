Kamon Showcase
==============

This is a quick a dirty app used to demostrate a few common situations that people might face when working with Kamon to monitor Play and Akka applications, it will get a lot better but good enough to start!

The docker image is here: https://github.com/kamon-io/docker-grafana-graphite and you will need to change the (storage schema)[1] to be 2 seconds so it matches the configured tick interval in Kamon. That is not recommended for production usage, it was made like that just to ensure quick feedback during the presentation!

There are some slides on ODP and PDF format (akka-and-play-under-the-hood files) and a grafana-dashboard.json export of the dashboard that was used during the presentation. The commands used to reproduce the scenarios were:

### Blocking code in actions:
```
Non blocking example:
ab -k -c 100 -n 500000 http://localhost:9000/

Blocking code:
ab -k -c 100 -n 500000 http://localhost:9000/blocking-code
ab -k -c 100 -n 500000 http://localhost:9000/blocking-code-async
```

### Forgetting about compartmentalization:
```
Fast and Slow actors:
ab -k -c 100 -n 500000 http://localhost:9000/fast-actor
ab -k -c 100 -n 500000 http://localhost:9000/slow-actor

The code in the repo is already assigning a custom dispatcher for the slow actor so you might
want to remove it for the problem to be actually simualted.
```

### The lagging actor
```
watch -n 1 -c "curl http://localhost:9000/lagging-actor"
```

### Logging too much
```
Actually, fast-actor is the guy logging on every message, just food it with requests and you will see!
ab -k -c 100 -n 500000 http://localhost:9000/fast-actor
```

### Using managed blocking
```
ab -k -c 100 -n 500000 http://localhost:9000/managed-blocking
```

### Keeping an eye on garbage collection
```
ab -k -c 100 -n 500000 http://localhost:9000/waster
```

Totally open to suggestions and feedback, expect a blog post to be shared soon!


[1]: https://github.com/kamon-io/docker-grafana-graphite/blob/master/graphite/storage-schemas.conf#L3
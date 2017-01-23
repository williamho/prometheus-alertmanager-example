# Prometheus Alertmanager Example

This is an example application that demonstrates using Prometheus Alertmanager
using docker-compose. It exposes an HTTP server on port 9999, that has two routes:

* `/`: Returns the text "Hello" and increments a counter `example_counter`.
* `/metrics`: Returns the above metric, to be scraped by Prometheus.

## Config

This example app is configured with an alterting rule that will fire when
the `example_counter` has not been incremented in the past 10 seconds.

The Prometheus alerting rules can be found in
[prometheus/alerting.rules](prometheus/alerting.rules).
Documentation on the Alerting rules syntax can be found on the
[Alerting Rules](https://prometheus.io/docs/alerting/rules/) page of the
Prometheus docs.

Prometheus is set up to point to Alertmanager via the `-alertmanager.url`
command line flag.

If you want to run this with real HipChat/PagerDuty/etc integration, edit the
[alertmanager/alertmanager.yml](alertmanager/alertmanager.yml) file with the
appropriate config keys. Consult the
[Alertmanager config](https://prometheus.io/docs/alerting/configuration/)
docs for examples.

## Run

1. Install sbt, docker, and docker-compose.

2. Run `sbt` from the root directory.

3. Type `dockerComposeUp` in the sbt console. This will fetch the relevant
   docker images and run them with docker-compose.

   On success, it will output something like the following:

   ```
   The following endpoints are available for your local instance: 658474
   +--------------+-----------------+-------------+--------------+----------------+--------------+---------+
   | Service      | Host:Port       | Tag Version | Image Source | Container Port | Container Id | IsDebug |
   +==============+=================+=============+==============+================+==============+=========+
   | alertmanager | 172.19.0.1:9093 | latest      | defined      | 9093           | 68170cd42e0e |         |
   | example-app  | 172.19.0.1:9999 | latest      | defined      | 9999           | f31992a9711e |         |
   | prometheus   | 172.19.0.1:9090 | latest      | defined      | 9090           | 7c13bbe2bbf5 |         |
   +--------------+-----------------+-------------+--------------+----------------+--------------+---------+
   Instance commands:
   1) To stop instance from sbt run:
      dockerComposeStop 658474
   2) To open a command shell from bash run:
      docker exec -it <Container Id> bash
   3) To view log files from bash run:
      docker-compose -p 658474 -f /var/folders/yx/s18c07ks46g84z0bh6x3_2fh0000gp/T/compose-updated1820909411141947885.yml logs -f
   4) To execute test cases against instance from sbt run:
      dockerComposeTest 658474
   >
   ```

   NOTE: If using Docker for Mac, the services will be available at `localhost`
   rather than the hostname displayed in the table above. The rest of the README
   will assume it's available on localhost.

4. Open Prometheus at http://localhost:9090 and note that the `no_hits` alert
   on the "Alerts" tab should be firing after 10 seconds, since the example
   app is not receiving any traffic.

5. Open Alertmanager at http://localhost:9093 and note that `dev-pager` is
   receiving an notification for `no_hits`.

6. Run the following in a shell:

   ```bash
   while true; do curl -s http://localhost:9999/; sleep 1; done
   ```

   This will make requests to the app once per seconds, incrementing the
   `example_counter` counter. Note that the alert should be resolved in
   Prometheus and Alertmanager after a
   short delay.

7. Stop the command in step (6) with Ctrl+C

8. After some time, note that the alert is fired again.

9. Stop the Docker containers using the `dockerComposeStop` command from the
   output in step (3)


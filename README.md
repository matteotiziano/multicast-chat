# Multicast Chat

Java implementation of a multicast chat guaranteeing:

* causal ordering of the massages using vector clocks
* integrity of the messages using SHA-1 hashing
* reliable message delivery over UDP multicast socket

## Introduction

The package is structured with a MVC design pattern, and the view and controller interacts through Event-Listener mechanisms. The causality is ensured by `CausalHandler` which compares the local vector clock with the vector clock included in the received message; whenever an inconsistency is detected, a retransmission request is sent to the original sender of the lost messages. Every received message is checked against the computed SHA-1 hash and the hash included in the header: if the hashes are different a retransmission request is sent. Retransmission requests are handled by a separate thread in `RepeaterHandler`.

## How to use

In order to test the chat in localhost, two instances of `Application` should be run having dual `Config.REPEATER_PORT_SERVER` and `Config.REPEATER_PORT_CLIENT`. The provided bash script `test.sh` will compile the code from command line and run two instances of `Application` using the following interface:

```
$ Application groupIP groupPort TTL repeaterPortServer repeaterPortClient
```

where

* `groupIP` is the IP of the multicast group
* `groupPort` is the port of the multicast group
* `TTL` is the time-to-live of the messages
* `repeaterPortServer` is the port accepting request for message retransmission
* `repeaterPortClient` is the port sending request for message retransmission


## Licence

MIT Licence. Copyright (c) 2015 Matteo Maggioni

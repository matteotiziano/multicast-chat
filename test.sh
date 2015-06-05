#! /bin/bash

javac -d bin/ -cp src src/view/Application.java

java -cp bin view.Application 230.0.0.1 4000 1 4001 4002 &
java -cp bin view.Application 230.0.0.1 4000 1 4002 4001

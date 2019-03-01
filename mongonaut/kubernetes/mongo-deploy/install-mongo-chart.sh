#!/bin/zsh

helm install --name mongo-mongonaut \
  --set persistence.enabled=false,mongodbRootPassword=mongonautRoot,mongodbUsername=mongonaut,mongodbPassword=mongonaut1234,mongodbDatabase=mongonaut \
    stable/mongodb

# Kubernetes Labels

We will use node labels to assign pods to specific nodes, using the selector feature.

To create a node label use:
```
kubectl label nodes mtd-node-1 node=1
```
The first parameter is hte nodename and second is the label.

Add `--overwrite`to change an existing label.

Our labels will be numerical to easily distinguish them as well as converting to integers later on in our scheduler.

To get a node with specific label use:
```
kubectl get nodes --selector node=1
```


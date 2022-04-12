# Kubernetes Labels

We will use node labels to assign pods to specific nodes, using the selector feature.

To create a node label use:
```
kubectl label nodes mtd-node-1 mtd/node=active
```
The first parameter is hte nodename and second is the label.

Add `--overwrite`to change an existing label.

Our labels will be numerical to easily distinguish them as well as converting to integers later on in our scheduler.

To get a node with specific label use:
```
kubectl get nodes --selector node=1
```

One way to move a pod between two nodes is to move a label from one node to another and then use `rollout restart`. For example:

```
kubectl label nodes mtd-node-1 mtd/node= --overwrite=true
kubectl label nodes mtd-node-2 mtd/node=active
kubectl rollout restart deployment deploymentName
```

To show node labels use:
```
kubectl get nodes --show-labels
```

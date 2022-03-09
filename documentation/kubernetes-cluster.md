# Kubernetes cluster installation

## Requeirements
* Docker
* CNI plugin (We use Weaver NET)

## Setup
Instructions for Ubuntu 20.04 LTS

### All Nodes
1. Update the host OS.
```
sudo apt update
sudo apt -y upgrade
```

2. Install the Docker runtime environment
```
sudo apt install -y docker.io
```

2. Allow iptables to see bridged traffic.
```
cat <<EOF | sudo tee /etc/modules-load.d/k8s.conf
br_netfilter
EOF

cat <<EOF | sudo tee /etc/sysctl.d/k8s.conf
net.bridge.bridge-nf-call-ip6tables = 1
net.bridge.bridge-nf-call-iptables = 1
EOF
sudo sysctl --system
```

3. Reboot the entire server.
```
sudo reboot now
```

4. Check the status of the netfilter.
```
lsmod | grep br_netfilter
```
The output should look like
```
br_netfilter           28672  0
bridge                176128  1 br_netfilter
```

5. Add the kubernetes repository. Begin with installing curl & dependencies.
```
sudo apt-get install -y apt-transport-https ca-certificates curl
```

6. Download and save the kubernetes keyrings. 
```
sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
```

7. Add Kubernetes to the repository.
```
echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
```

6. Install kubeadm and kubectl.
```
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
```

7. Configure Docker's cgroup driver and restart the docker service.:
```
sudo su
cat > /etc/docker/daemon.json <<EOF
{
    "exec-opts": ["native.cgroupdriver=systemd"],
    "log-driver": "json-file",
    "log-opts": {
        "max-size": "100m"
    },
    "storage-driver": "overlay2"
}
EOF
systemctl restart docker.service
```

## Only Master/Control Node
1. Start cluster, replace IP of master (api) in this command.
```
kubeadm init --pod-network-cidr 10.244.0.0/16 --apiserver-advertise-address=129.16.123.61
```

2. If root user, do `logout` or `exit` if in a SSH session.

3. As a regular user, copy the kubeadm config so that it can be used by kubectl.
```
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

4. Wait for master node to become ready. Check with.
```
kubectl get nodes
```

5.  Install Weave NET:
```
kubectl apply -f "https://cloud.weave.works/k8s/net?k8s-version=$(kubectl version | base64 | tr -d '\n')"
```




### Worker nodes

1. Every worker node run:
```
kubeadm join 129.16.123.61:6443 --token 8qzrtq.3xdoqo0928hd66fq --discovery-token-ca-cert-hash sha256:09018f5e38a5a5d51281e717896748b7bc2f1622ece6401e3e7eea7cf672c43d
```

2. If nothing is happening, generate a new token.
```
kubeadm token create --print-join-command
```

3. Check if the node has joined the cluster, run on Master node.
```
kubectl get nodes
```

### Test running a pod

From the master node run a nginx pod:  
```
kubectl run nginx --image=nginx
```

Check if the pod is running running:  
```
kubectl get pods -o wide
```

To delete all test pods, run
```
kubectl delete pods --all
```


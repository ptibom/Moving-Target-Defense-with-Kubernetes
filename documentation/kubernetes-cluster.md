# Kubernetes cluster installation

## Requeirements
* Docker
* CNI plugin (We use Weaver NET)

## Setup
Instructions for Ubuntu 20.04 LTS

### All Nodes
1. `sudo apt update`
2. `sudo apt upgrade`
3. `sudo apt install docker.io`
4. Allow iptables to see bridged traffic.
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

5. Check status `lsmod | grep br_netfilter`

6. Install kubeadm `sudo apt-get install -y apt-transport-https ca-certificates curl`
7. `sudo curl -fsSLo /usr/share/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg`
8. `echo "deb [signed-by=/usr/share/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list`

9. Run
```
sudo apt-get update
sudo apt-get install -y kubelet kubeadm kubectl
sudo apt-mark hold kubelet kubeadm kubectl
sudo su
```

10. Configure cgroup driver:
```
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
```

11. Run `systemctl restart docker.service`

## Only Master/Control Node
1. Start cluster, replace IP of master (api) in this command.
```
kubeadm init --pod-network-cidr 10.244.0.0/16 --apiserver-advertise-address=129.16.123.61
```

2. If root user, do `logout`

3. Run the following as a regular user.
```
mkdir -p $HOME/.kube
sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

4. Wait for master node to become ready. Check with `kubectl get nodes`

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

### Test running a pod

From the master node run:  
`kubectl run nginx --image=nginx`

Check if its running:  
`kubectl get pods -o wide`


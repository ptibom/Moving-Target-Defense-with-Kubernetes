# Kubernetes cluster installation

## Requeirements
* Docker
* CNI plugin (We use Weaver NET)

## Setup
Instructions for Ubuntu 20.04 LTS

### Master / Control node
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

11. (Only for Master node) Start cluster, replace IP of master (api) in this command.
```
kubeadm init --pod-network-cidr 10.244.0.0/16 --apiserver-advertise-address=129.16.123.61
```

### Worker nodes

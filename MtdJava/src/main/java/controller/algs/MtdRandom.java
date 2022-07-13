/*
 * Moving Target Defense with Kubernetes
 * Copyright (C) 2022  Philip Tibom and Max Buck
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package controller.algs;

import model.kubernetes.*;
import model.kubernetes.exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MtdRandom implements IMtdAlg {

    private IDeployment currentDeployment;
    private int timeBetweenSwap; // Todo maybe add random time.
    private int currentIndex;
    private int currentDeploymentIndex;
    private INode currentNode;
    private IPod currentPod;
    // Label key for the active k8 node
    private static final String LABEL_KEY = "mtd/node";
    // Label value for the active K8 node.
    private static final String LABEL_VALUE = "active";
    // Enable while running test suite
    private boolean testSuite = false;
    // Marks if it's the first iteration of running the alg.
    private boolean isRunning = false;
    private List<IDeployment> deployments;

    public MtdRandom(List<IDeployment> deployments, int timeBetweenSwap) throws Exception {
        this.deployments = deployments;
        this.timeBetweenSwap = timeBetweenSwap;
        try {
            // Pick a random node and deployment, from available nodes/deployments, to start the alg with.
            List<INode> nodeList = NodeTools.getWorkerNodes();
            Random random = new Random();
            currentIndex = random.nextInt(nodeList.size());
            currentNode = nodeList.get(currentIndex);
            currentDeploymentIndex = random.nextInt(this.deployments.size());
            currentDeployment = deployments.get(currentDeploymentIndex);
        } catch (NodeNotFoundException e) {
        } catch (IllegalArgumentException e) {
            throw new Exception("Not enough worker nodes."); // todo create a custom exception
        }
    }

    @Override
    public List<String> run() {
        return run(0);
    }

    @Override
    public List<String> run(int nSwaps) {
        try {
            List<String> log = new ArrayList<>();

            int i = 0;
            // Make it loop infinitely if nSwaps = 0.
            if (nSwaps == 0) {
                i = -1;
            }
            while (i < nSwaps) {
                // Check if initial iteration
                if (!isRunning) {
                    isRunning = true;
                    // Clean up old labels (if alg ran before)
                    List<INode> nodeList = NodeTools.getWorkerNodes();
                    for (INode node : nodeList) {
                        node.deleteLabel(LABEL_KEY);
                    }
                    // Add Active label to the randomly selected current node
                    currentNode.addLabel(LABEL_KEY, LABEL_VALUE);
                    try {
                        // Delete old deployment if exists.
                        IDeployment oldDeployment = new Deployment(currentDeployment.getName(), "default");
                        oldDeployment.delete();
                    } catch (DeploymentNotFoundException | DeploymentDeleteException ignored) {
                    }
                    // Apply deployment, spins up pods etc.
                    currentDeployment.apply();

                    // Wait a set time
                    Thread.sleep(timeBetweenSwap);
                    continue;
                }
                // Refresh pod statuses.

                System.out.println("#Pods on node: " + currentNode.getPods().size());
                while (currentNode.getPods().size() == 0) {
                    System.out.println("Zero pods, waiting. Node: " + currentNode.getName());
                    Thread.sleep(200);
                    currentNode = new Node(currentNode.getName());
                }

                if (testSuite) {
                    System.out.println("Current node name: " + currentNode.getName());
                    log.add(currentNode.getName());
                }

                IPod foundNewPod = null;
                IPod tmpPod = currentNode.getPods().get(0);
                // Needed when node has only 1 pod, in case currentPod is not set.
                if (currentNode.getPods().size() == 1
                        && (tmpPod.getPhase().equalsIgnoreCase("pending")
                        || tmpPod.getPhase().equalsIgnoreCase("running"))) {
                    foundNewPod = tmpPod;
                    System.out.println("Node has 1 pod, Found pod to wait for: " + foundNewPod.getName());
                }

                while (foundNewPod == null) {
                    for (IPod pod : currentNode.getPods()) {
                        if (!pod.getName().equals(currentPod.getName())
                                && (pod.getPhase().equalsIgnoreCase("pending")
                                || pod.getPhase().equalsIgnoreCase("running"))) {
                            foundNewPod = pod;
                            System.out.println("Found pod to wait for: " + foundNewPod.getName());
                            break;
                        }
                    }
                    // Refresh podlist if we did not find the correct one.
                    if (foundNewPod == null) {
                        Thread.sleep(1000);
                        currentNode = new Node(currentNode.getName());
                    }
                }

                while (foundNewPod.getPhase().equalsIgnoreCase("pending")) {
                    System.out.println("Waiting for pending pod: " + foundNewPod.getName());
                    Thread.sleep(1000);
                    currentNode = new Node(currentNode.getName());
                    foundNewPod = new Pod(foundNewPod.getName());
                }

                List<INode> nodeList = NodeTools.getWorkerNodes();
                System.out.println("Try to delete: " + currentNode.getName());
                nodeList.removeIf(tmpNode -> tmpNode.getName().equals(currentNode.getName()));

                System.out.println("======================= LIST =================");
                for (INode n : nodeList) {
                    System.out.println(n.getName());
                }
                System.out.println("------------");

                Random random = new Random();
                int randIntNode = random.nextInt(nodeList.size());
                int randIntDeployment = random.nextInt(deployments.size());

                // Swap active label on nodes.
                currentNode.deleteLabel(LABEL_KEY);
                currentNode = nodeList.get(randIntNode);
                currentNode.addLabel(LABEL_KEY, LABEL_VALUE);

                // Swap active deployment
                IDeployment oldDeployment = currentDeployment;
                currentDeployment = deployments.get(randIntDeployment);

                if (oldDeployment == currentDeployment) {
                    currentDeployment.rolloutRestart(); // Patches if same deployment.
                }
                else {
                    currentDeployment.apply(); // Apply if a different deployment.
                }

                // Update pod statuses.
                currentNode = new Node(currentNode.getName());
                while (currentNode.getPods().size() == 0)  {
                    Thread.sleep(100);
                    currentNode = new Node(currentNode.getName());
                }
                currentPod = currentNode.getPods().get(0);
                Thread.sleep(timeBetweenSwap);

                // Make it loop infinitely if nSwaps = 0.
                if (nSwaps != 0) {
                    i++;
                }
            }

            return log;
        } catch (NodeNotFoundException | NodeLabelException | ApplyException | InterruptedException |
                 PodNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void setTimeBetweenSwap(int milliseconds) {
        this.timeBetweenSwap = milliseconds;
    }

    @Override
    public int getTimeBetweenSwap() {
        return this.timeBetweenSwap;
    }

    public void setTestSuite(boolean active) {
        testSuite = active;
    }
}

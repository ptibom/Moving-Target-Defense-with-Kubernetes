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

import model.encapsulation.*;
import model.encapsulation.exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MtdRandom implements IMtdAlg {

    private IDeployment deployment;
    private int timeBetweenSwap; // Todo maybe add random time.
    private int currentIndex;
    private INode currentNode;
    private IPod currentPod;
    private static final String LABEL_KEY = "mtd/node";
    private static final String LABEL_VALUE = "active";
    private boolean testSuite = false;
    private boolean isRunning = false;

    public MtdRandom(IDeployment deployment, int timeBetweenSwap) throws Exception {
        this.deployment = deployment;
        this.timeBetweenSwap = timeBetweenSwap;
        try {
            List<INode> nodeList = NodeTools.getWorkerNodes();
            Random random = new Random();
            currentIndex = random.nextInt(nodeList.size());
            currentNode = nodeList.get(currentIndex);
        } catch (NodeNotFoundException e) {
        } catch (IllegalArgumentException e) {
            throw new Exception("Not enough worker nodes."); // todo create a custom exception
        }
    }

    @Override
    public List<String> run(int nSwaps) {
        try {
            List<String> log = new ArrayList<>();
            for (int i = 0; i < nSwaps; i++) {
                if (!isRunning) {
                    isRunning = true;
                    List<INode> nodeList = NodeTools.getWorkerNodes();
                    for (INode node : nodeList) {
                        node.deleteLabel(LABEL_KEY);
                    }
                    currentNode.addLabel(LABEL_KEY, LABEL_VALUE);
                    try {
                        // Delete old deployment if exists.
                        IDeployment oldDeployment = new Deployment(deployment.getName(), "default");
                        oldDeployment.delete();
                    } catch (DeploymentNotFoundException | DeploymentDeleteException ignored) {
                    }
                    deployment.apply();

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
                int randInt = random.nextInt(nodeList.size());

                // Swap active label on nodes.
                currentNode.deleteLabel(LABEL_KEY);
                currentNode = nodeList.get(randInt);
                currentNode.addLabel(LABEL_KEY, LABEL_VALUE);

                deployment.rolloutRestart();

                // Update pod statuses.
                currentNode = new Node(currentNode.getName());
                while (currentNode.getPods().size() == 0)  {
                    Thread.sleep(100);
                    currentNode = new Node(currentNode.getName());
                }
                currentPod = currentNode.getPods().get(0);
                Thread.sleep(timeBetweenSwap);
            }

            return log;
        } catch (NodeNotFoundException | NodeLabelException | ApplyException | InterruptedException |
                 PodNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Override
    public void stop() {

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

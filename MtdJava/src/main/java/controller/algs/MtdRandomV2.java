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
/**
 * This is a rewritten version of V1 to make it more comprehensible.
 */
public class MtdRandomV2 implements IMtdAlg {

    private int timeBetweenSwap = 5000;
    // Label key for the active k8 node
    private static final String LABEL_KEY = "mtd/node";
    // Label value for the active K8 node.
    private static final String LABEL_VALUE = "active";
    private INode currentNode = null;
    private IDeployment currentDeployment = null;
    private List<IDeployment> deployments;



    /**
     *
     * @param deployments A list of deployments that will be randomized during the MTD execution.
     * @param timeBetweenSwap The time the MTD should wait before swapping to a different node.
     */
    public MtdRandomV2(List<IDeployment> deployments, int timeBetweenSwap) {
        this.timeBetweenSwap = timeBetweenSwap;
        this.deployments = deployments;
    }

    /**
     * Used for running the MTD algorithm forever
     * @return Returns a list of logging events
     */
    @Override
    public List<String> run() {
        return run(0);
    }

    /**
     * Used for running the MTD algorithm for a number of swaps, then it cancels.
     * @param nSwaps The number of times the algorithm should swap before it cancels.
     * @return Returns a list of logging events
     */
    @Override
    public List<String> run(int nSwaps) {
        List<String> log = new ArrayList<>(); // Logs the swaps.

        // Delete old deployment if exists.
        System.out.println("Starting MTD alg.");
        try {
            IDeployment oldDeployment = new Deployment(deployments.get(0).getName(), "default");
            System.out.println("Deleting old deployment");
            oldDeployment.delete();
        } catch (DeploymentNotFoundException | DeploymentDeleteException ignored) {
        }

        // Make it loop infinitely if nSwaps = 0.
        int i = 0;
        // Make it loop infinitely if nSwaps = 0.
        if (nSwaps == 0) {
            i = -1;
        }
        while (i < nSwaps) {
            try {
                // Delete active labels if exists.
                List<INode> nodeList = NodeTools.getWorkerNodes();
                for (INode node : nodeList) {
                    if (node.getLabels().containsKey(LABEL_KEY)) {
                        System.out.println("Deleting label from: " + node.getName());
                        node.deleteLabel(LABEL_KEY);
                    }
                }
            }
            catch (NodeNotFoundException | NodeLabelException ignored) {
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException("Not enough worker nodes.");
            }
            try {
                // Get all available worker nodes.
                List<INode> nodeList = NodeTools.getWorkerNodes();
                // If currentNode has been selected before, then remove it from the list to prevent it from getting chosen again.
                if (currentNode != null) {
                    System.out.println("Removing " + currentNode.getName() +  " from next random selection.");
                    nodeList.removeIf(tmpNode -> tmpNode.getName().equals(currentNode.getName()));
                }
                // Choose a random node out of the remaining available, then set it to active.
                Random random = new Random();
                int randIntNode = random.nextInt(nodeList.size());
                currentNode = nodeList.get(randIntNode);
                System.out.println("Randomly selected node: " + currentNode.getName() + ", adding active label.");
                currentNode.addLabel(LABEL_KEY, LABEL_VALUE);
                // Choose a random deployment, can select same again
                int randIntDeployment = random.nextInt(deployments.size());

                // Swap active deployment
                IDeployment oldDeployment = currentDeployment;
                currentDeployment = deployments.get(randIntDeployment);
                System.out.println("Randomly selected Deployment: " + currentDeployment.getFileName());

                log.add(String.format("Node: %s, Deployment: %s", currentNode.getName(), currentDeployment.getFileName()));

                // If same deployment, do a restart to swap node.
                if (oldDeployment == currentDeployment) {
                    System.out.println("Doing a rolloutRestart on deployment.");
                    currentDeployment.rolloutRestart(); // Patches if same deployment.
                }
                // If not same, then apply the different deployment config.
                else {
                    System.out.println("Doing an Apply on deployment.");
                    currentDeployment.apply(1); // Apply if a different deployment.
                }

                // Find if correct pod is running.
                System.out.println("Trying to find the new pod...");
                while (!(currentNode.getPods().size() == 1 && currentNode.getPods().get(0).getPhase().equalsIgnoreCase("running"))) {
                    System.out.println("Did not find new pod, waiting 1 second.");
                    Thread.sleep(1000);
                    // Refresh node
                    currentNode = new Node(currentNode.getName());
                }
                System.out.println("Pod found, waiting " + timeBetweenSwap + "ms before next iteration.");
                Thread.sleep(timeBetweenSwap);
                System.out.println("============= Iteration Done ============\n");

            } catch (NodeNotFoundException | NodeLabelException | ApplyException | InterruptedException |
                     PodNotFoundException e) {
                e.printStackTrace();
            }
            // Make it loop infinitely if nSwaps = 0.
            if (nSwaps != 0) {
                i++;
            }
        }
        return log;
    }

    @Override
    public void setTimeBetweenSwap(int milliseconds) {
        timeBetweenSwap = milliseconds;
    }

    @Override
    public int getTimeBetweenSwap() {
        return timeBetweenSwap;
    }
}

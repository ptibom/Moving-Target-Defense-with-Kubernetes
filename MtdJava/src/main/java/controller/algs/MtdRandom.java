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

import model.encapsulation.IDeployment;
import model.encapsulation.INode;
import model.encapsulation.NodeTools;
import model.encapsulation.exception.ApplyException;
import model.encapsulation.exception.DeploymentNotFoundException;
import model.encapsulation.exception.NodeLabelException;
import model.encapsulation.exception.NodeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MtdRandom implements IMtdAlg {

    private IDeployment deployment;
    private int timeBetweenSwap; // Todo maybe add random time.
    private int currentIndex;
    private INode currentNode;
    private static final String LABEL_KEY = "mtd/node";
    private static final String LABEL_VALUE = "active";
    private boolean testSuite = false;
    private boolean isRunning = false;

    public MtdRandom(IDeployment deployment, int timeBetweenSwap) {
        this.deployment = deployment;
        this.timeBetweenSwap = timeBetweenSwap;
        try {
            List<INode> nodeList = NodeTools.getWorkerNodes();
            Random random = new Random();
            currentIndex = random.nextInt(nodeList.size());
            currentNode = nodeList.get(currentIndex);
        } catch (NodeNotFoundException e) {
        }
    }

    @Override
    public List<String> run() {
        try {
            List<String> log = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                if (!isRunning) {
                    isRunning = true;
                    currentNode.addLabel(LABEL_KEY, LABEL_VALUE);
                    deployment.apply();
                    continue;
                }
                if (testSuite) {
                    System.out.println(deployment.getPods().get(0).getNodeName()); // Todo correct the print.
                    log.add(deployment.getPods().get(0).getNodeName());
                }

                /*
                    todo, failed first iteration.
                    minikube-m02
                    Try to delete: minikube-m03
                    ======================= LIST =================
                    minikube-m02
                    minikube-m04

                 */

                List<INode> nodeList = NodeTools.getWorkerNodes();
                System.out.println("Try to delete: " + currentNode.getName());
                nodeList.removeIf(tmpNode -> tmpNode.getName().equals(currentNode.getName()));
                //nodeList.remove(currentNode); // Todo Fails once, minikube-m03 twice.

                System.out.println("======================= LIST =================");
                for (INode n : nodeList) {
                    System.out.println(n.getName());
                }
                System.out.println(" ");

                Random random = new Random();
                int randInt = random.nextInt(nodeList.size());

                // Swap active label on nodes.
                currentNode.deleteLabel(LABEL_KEY);
                currentNode = nodeList.get(randInt);
                currentNode.addLabel(LABEL_KEY, LABEL_VALUE);

                deployment.rolloutRestart();

                Thread.sleep(timeBetweenSwap);
            }

            return log;
        } catch (NodeNotFoundException | NodeLabelException | ApplyException | InterruptedException e) {
            e.printStackTrace();
        } catch (DeploymentNotFoundException e) {
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

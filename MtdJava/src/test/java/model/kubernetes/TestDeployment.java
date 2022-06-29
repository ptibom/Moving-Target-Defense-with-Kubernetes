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

package model.kubernetes;

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import model.kubernetes.exception.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestDeployment {

    @BeforeAll
    static void init() {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void testInitDeployment() throws IOException, ApplyException, NodeNotFoundException, NodeLabelException {
        IDeployment deployment = new Deployment(new File("TestDeployment.yaml"));
        // Need to add labels to nodes to correctly start the pods from the deployment file
        String key = "mtd/node";
        INode node = new Node("minikube-m02");
        node.addLabel(key, "active");
        node = new Node("minikube-m03");
        node.deleteLabel(key);
        deployment.apply();
        assertNotNull(deployment);
    }

    @Test
    @Order(3)
    void testRolloutRestartDeployment() throws NodeNotFoundException, NodeLabelException, DeploymentNotFoundException, ApplyException, InterruptedException {
        String key = "mtd/node";
        String value = "active";
        INode node = new Node("minikube-m02");
        node.deleteLabel(key);
        String nodeName = "minikube-m03";
        node = new Node(nodeName);
        node.addLabel(key, value);
        IDeployment deployment = new Deployment("nginx-deployment", "default");
        deployment.rolloutRestart();
        Thread.sleep(5000);
        deployment = new Deployment("nginx-deployment", "default");
        List<IPod> podlist = deployment.getPods();
        IPod pod = podlist.get(0);
        assertEquals(nodeName, pod.getNodeName());
    }

    @Test
    @Order(2)
    void testPodList() throws DeploymentNotFoundException {
        Deployment deployment = new Deployment("nginx-deployment", "default");
        List<IPod> podlist = deployment.getPods();
        assertEquals(1, podlist.size());
    }

    @Test
    @Order(10)
    void testDeleteDeployment() throws InterruptedException, DeploymentDeleteException {
        try {
            IDeployment deployment = new Deployment("nginx-deployment", "default");
            deployment.delete();
            Thread.sleep(5000);
            assertThrows(DeploymentNotFoundException.class, () -> {
                IDeployment tmp = new Deployment("nginx-deployment", "default");
            });
        }
        catch (DeploymentNotFoundException e) {
            e.printStackTrace();
            // Pass the test if deployment is not found.
        }
    }

}

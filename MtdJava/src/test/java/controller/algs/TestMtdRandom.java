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

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;

import model.kubernetes.Deployment;
import model.kubernetes.IDeployment;
import model.kubernetes.IService;
import model.kubernetes.Service;
import model.kubernetes.exception.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestMtdRandom {

    @BeforeAll
    static void init() {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testRun() throws Exception {
        IDeployment deployment = new Deployment(new File("DeploymentPrintNode.yaml"));
        List<IDeployment> deployments = new ArrayList<>();
        deployments.add(deployment);
        MtdRandom mtdRandom = new MtdRandom(deployments, 5000);
        mtdRandom.setTestSuite(true);
        List<String> log = mtdRandom.run(10);
        String previousEntry = "";
        for (String s : log) {
            assertNotEquals(previousEntry, s);
            previousEntry = s;
        }
    }

    @Test
    @Disabled
    // Same test as "testRun" but longer -- And with node printing deployment.
    // minikube service lb-service --url
    void testWithLoadBalancer() throws Exception {
        IService service = new Service(new File("TestService.yaml"));
        service.apply();
        IDeployment deployment = new Deployment(new File("DeploymentPrintNode.yaml"));
        List<IDeployment> deployments = new ArrayList<>();
        deployments.add(deployment);
        MtdRandom mtdRandom = new MtdRandom(deployments, 5000);
        mtdRandom.setTestSuite(true);
        List<String> log = mtdRandom.run(10);
        String previousEntry = "";
        for (String s : log) {
            assertNotEquals(previousEntry, s);
            previousEntry = s;
        }
    }

    @AfterAll
    static void deleteDeployment() throws DeploymentDeleteException, DeploymentNotFoundException,
            KubeServiceDeleteException, KubeServiceNotFoundException {

        IDeployment deployment = new Deployment("nginx-deployment", "default");
        deployment.delete();
        IService service = new Service("lb-service", "default");
        service.delete();
    }

}

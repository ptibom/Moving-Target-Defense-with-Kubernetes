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

public class TestMtdRandomV2 {

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
        IMtdAlg mtdRandom = new MtdRandomV2();
        mtdRandom.run(deployments, 5000);
    }

}

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

package controller;

import controller.algs.IMtdAlg;
import controller.algs.MtdRandom;
import controller.algs.MtdRandomV2;
import model.kubernetes.Deployment;
import model.kubernetes.IDeployment;
import model.kubernetes.IService;
import model.kubernetes.Service;
import model.kubernetes.exception.ApplyException;
import view.MtdView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MtdController {
    SettingsController settingsController;
    MtdView mtdView = new MtdView();

    public MtdController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    /**
     * Runs the MTD algorithm. Currently uses either V2 or V3, hardcoded.
     * todo implement algorithm selection in the settings file
     */
    public void runMtd() {

        try {
            if (settingsController.isLoadBalancing()) {
                IService service = new Service(new File(settingsController.getServiceFileName()));
                service.apply();
            }
            List<IDeployment> deploymentList = new ArrayList<>();
            for (String filename : settingsController.getDeploymentFileNames()) {
                IDeployment deployment = new Deployment(new File(filename));
                deploymentList.add(deployment);
            }
            // This is where the algorithm is selected. Change the class to V3 or V2 before compiling.
            // Or implement alg selection from settings.
            IMtdAlg alg = new MtdRandomV2(deploymentList, 5000);
            alg.run();
        } catch (IOException e) {
            mtdView.printError(String.format("Could not find Deployment file."));
        } catch (ApplyException e) {
            mtdView.printError("Could not apply Service to cluster.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

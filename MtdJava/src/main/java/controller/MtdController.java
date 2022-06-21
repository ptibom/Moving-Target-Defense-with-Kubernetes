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
import model.Settings;
import model.encapsulation.Deployment;
import model.encapsulation.IDeployment;
import model.encapsulation.IService;
import model.encapsulation.Service;
import model.encapsulation.exception.ApplyException;
import view.MtdView;

import java.io.File;
import java.io.IOException;

public class MtdController {
    SettingsController settingsController;
    MtdView mtdView = new MtdView();

    public MtdController(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public void runMtd() {
        String fileName = "DeploymentPrintNode.yaml";
        try {
            if (settingsController.isLoadBalancing()) {
                IService service = new Service(new File(settingsController.getServiceFileName()));
                service.apply();
            }
            IDeployment deployment = new Deployment(new File(fileName));
            IMtdAlg alg = new MtdRandom(deployment, 5000);
            alg.run(10);
        } catch (IOException e) {
            mtdView.printError("Could not find file: " + fileName);
        } catch (ApplyException e) {
            mtdView.printError("Could not apply Service to cluster.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Select alg / controller.
            if settings.alg == 1:
                randomMtdController.run();
            elif settings.alg == 2:
                seqMtdController.run();
         */
    }
}

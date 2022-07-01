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

import controller.MenuController;
import controller.MtdController;
import controller.SettingsController;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;

import java.io.File;
import java.io.IOException;

public class MtdMain {
    public static void main(String[] args) {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (args.length == 0) {
            MenuController menuController = new MenuController();
            menuController.showMenu();
        }
        else if (args.length == 1) {
           SettingsController settingsController = new SettingsController();
            try {
                settingsController.loadSettings(new File(args[0]));
                MtdController mtdController = new MtdController(settingsController);
                mtdController.runMtd();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else {
            System.out.println("Too many arguments. Argument should be a settings.yaml file");
        }
    }

    // todo
    // - Time between swaps
    // - Random interval time
    // - Alg selection (maybe)
    // - Log to file
    // - Log to console

}

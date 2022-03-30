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
import controller.SettingsController;

public class MtdMain {


    public static void main(String[] args) {
        if (args.length > 0 && !args[0].equals("nomenu")) {
            MenuController menuController = new MenuController();
            menuController.showMenu();
        }
        else {
            // TODO: Run from saved settings
           SettingsController settingsController = new SettingsController();

        }
    }



    // Flow 1: Menu -> Matar in settings -> sparar settings -> startar MTD baserat på settings.
    // Flow 2: Startar mtd baserat på settings.

    // Start commands.
    // java mtd.jar
    // java mtd.jar settings/config1.yaml

}
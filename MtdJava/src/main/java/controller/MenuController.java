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

import view.MenuView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Scanner;

public class MenuController {
    MenuView menuView = new MenuView();
    SettingsController settingsController;

    public void showMenu() {
        menuView.showMenu();
        Scanner sc = new Scanner(System.in);

        boolean optionSelected = false;
        while (!optionSelected) {
            String input = sc.nextLine();
            switch (input.strip()) {
                case "1":
                    startSelected();
                    optionSelected = true;
                    break;
                case "2":
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option, try again");
            }
        }
    }

    public void startSelected() {
        menuView.printLoadBalancerQuestion();
        settingsController = new SettingsController();

        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        boolean loadBalancing;
        while (true) {
            if (input.strip().equalsIgnoreCase("y") || input.strip().equalsIgnoreCase("yes")) {
                loadBalancing = true;
                break;
            }
            else if (input.strip().equalsIgnoreCase("n") || input.strip().equalsIgnoreCase("no")) {
                loadBalancing = false;
                break;
            }
            else {
                menuView.repeatLoadBalancerQuestion();
            }
        }
        System.out.println("Starting MTD.");
        settingsController.getSettings().setServiceEnabled(loadBalancing);
        MtdController mtdController = new MtdController(settingsController);
        mtdController.runMtd(); // todo, run in async thread?
    }
}

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
                    SettingsController settingsController = new SettingsController();
                    askLoadBalancerQuestion(settingsController);
                    askFileLoggingQuestino(settingsController);
                    askConsoleLoggingQuestion(settingsController);
                    MtdController mtdController = new MtdController(settingsController);
                    mtdController.runMtd(); // todo, run in async thread?
                    optionSelected = true;
                    break;
                case "2":
                    optionSelected = true;
                    break;
                case "3":
                    optionSelected = true;
                    break;
                case "4":
                    System.exit(0);
                    break;
                default:
                    menuView.printInvalidInput();
            }
        }
    }

    public void askLoadBalancerQuestion(SettingsController settingsController) {
        menuView.printLoadBalancerQuestion();
        boolean answer = askYesNo();
        settingsController.setLoadBalancerSetting(answer);
    }


    public void askFileLoggingQuestino(SettingsController settingsController) {
        menuView.printFileLoggingQuestion();
        boolean answer = askYesNo();
        settingsController.setFileLoggingSetting(answer);
    }

    public void askConsoleLoggingQuestion(SettingsController settingsController) {
        menuView.printConsoleLoggingQuestion();
        boolean answer = askYesNo();
        settingsController.setConsoleLoggingSetting(answer);
    }

    private boolean askYesNo() {
        Scanner sc = new Scanner(System.in);
        boolean answer;
        while (true) {
            String input = sc.nextLine();
            if (input.strip().equalsIgnoreCase("y") || input.strip().equalsIgnoreCase("yes")) {
                answer = true;
                break;
            }
            else if (input.strip().equalsIgnoreCase("n") || input.strip().equalsIgnoreCase("no")) {
                answer = false;
                break;
            }
            else {
                menuView.printInvalidInput();
            }
        }
        return answer;
    }
}

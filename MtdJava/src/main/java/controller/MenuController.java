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

import java.io.File;
import java.util.Scanner;

public class MenuController {
    MenuView menuView = new MenuView();

    public void showMenu() {
        menuView.showMenu();
        Scanner sc = new Scanner(System.in);

        boolean optionSelected = false;
        while (!optionSelected) {
            String input = sc.nextLine();
            switch (input.strip()) {
                case "1":
                    SettingsController settingsController = new SettingsController();

                    boolean answer = askLoadBalancerQuestion(settingsController);
                    if (answer) {
                        askServiceFileQuestion(settingsController);
                    }
                    askFileLoggingQuestion(settingsController);
                    askConsoleLoggingQuestion(settingsController);
                    askDeploymentFileQuestion(settingsController);

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

    public boolean askLoadBalancerQuestion(SettingsController settingsController) {
        menuView.printLoadBalancerQuestion();
        boolean answer = inputYesNo();
        settingsController.setLoadBalancing(answer);
        return answer;
    }


    public void askFileLoggingQuestion(SettingsController settingsController) {
        menuView.printFileLoggingQuestion();
        boolean answer = inputYesNo();
        settingsController.setFileLogging(answer);
    }

    public void askConsoleLoggingQuestion(SettingsController settingsController) {
        menuView.printConsoleLoggingQuestion();
        boolean answer = inputYesNo();
        settingsController.setConsoleLogging(answer);
    }

    public void askServiceFileQuestion(SettingsController settingsController) {
        menuView.printServiceFileNameQuestion();
        String fileName = inputFileName();
        settingsController.setServiceFileName(fileName);
    }

    public void askDeploymentFileQuestion(SettingsController settingsController) {
        menuView.printDeploymentFileNameQuestion();
        String fileName = inputFileName();
        settingsController.setDeploymentFileName(fileName);
    }

    private boolean checkFileExists(String fileName) {
        return new File(fileName).exists();
    }

    private String inputFileName() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine().strip();
            if (input.matches("^[A-z\\d]+(\\.yaml|\\.yml)$")) {
                if (checkFileExists(input)) {
                    return input;
                }
                else {
                    menuView.printFileNotExists();
                }
            }
            else {
                menuView.printInvalidInput();
            }
        }
    }

    private boolean inputYesNo() {
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

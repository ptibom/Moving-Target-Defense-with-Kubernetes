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

package view;

public class MenuView {
    public void showMenu() {
        System.out.println("Moving Target Defense");
        System.out.println("=====================");
        System.out.println("Menu Options");
        System.out.println("1. Create & Run Settings File");
        System.out.println("2. Load & Run Settings File");
        //System.out.println("3. Edit Settings File");
        System.out.println("3. Exit");
        System.out.println("---------------------");
        System.out.println("Make a selection (number):");
    }

    public void printLoadBalancerQuestion() {
        System.out.println("\n--- MTD Wizard ---");
        System.out.println("Do you want load balancing? (y/n)");
    }

    public void printFileLoggingQuestion() {
        System.out.println("Do you want logging to file? (y/n)");
    }

    public void printConsoleLoggingQuestion() {
        System.out.println("Do you want logging to console? (y/n)");
    }

    public void printServiceFileNameQuestion() {
        System.out.println("Type your Service YAML file name (including .yaml):");
    }

    public void printDeploymentFileNameQuestion() {
        System.out.println("Type your Deployment YAML file name (including .yaml):");
    }

    public void printInvalidInput(String message) {
        System.out.println("Invalid input. " + message + " Try again:");
    }

    public void printFileNotExists() {
        System.out.println("The file does not exist. Try again.");
    }

    public void printSettingsNameQuestion() {
        System.out.println("Name your settings file (including file ending .yaml or .yml):");
    }

    public void printLoadSettingsNameQuestion() {
        System.out.println("Type the filename of the settings file (yaml):");
    }
}

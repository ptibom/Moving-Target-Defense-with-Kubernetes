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

package model;

import java.util.ArrayList;
import java.util.List;

public class Settings {
    private String name;
    private boolean serviceEnabled = false; // LoadBalancer
    private String serviceFileName = "TestService.yaml";
    private List<String> deploymentFileNames = new ArrayList<>();
    private boolean logToConsole = true;
    private boolean logToFile = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isServiceEnabled() {
        return serviceEnabled;
    }

    public void setServiceEnabled(boolean serviceEnabled) {
        this.serviceEnabled = serviceEnabled;
    }

    public String getServiceFileName() {
        return serviceFileName;
    }

    public boolean isLogToConsole() {
        return logToConsole;
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }

    public boolean isLogToFile() {
        return logToFile;
    }

    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
    }

    public void setServiceFileName(String serviceFileName) {
        this.serviceFileName = serviceFileName;
    }

    public List<String> getDeploymentFileNames() {
        return deploymentFileNames;
    }

    public void setDeploymentFileNames(List<String> deploymentFileNames) {
        this.deploymentFileNames = deploymentFileNames;
    }
}

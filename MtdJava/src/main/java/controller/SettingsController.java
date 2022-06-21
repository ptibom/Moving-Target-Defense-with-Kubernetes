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

import model.Settings;

import java.io.File;

public class SettingsController {
    private Settings settings = new Settings();

    public Settings loadSettings(File file) {
        // todo load settings
        // settings = ...
        return settings;
    }

    public void saveSettings() {
        // todo save settings to file.
    }

    public Settings getSettings() {
        return settings;
    }

    public void setLoadBalancing(boolean enableLoadBalancer) {
        settings.setServiceEnabled(enableLoadBalancer);
    }

    public void setFileLogging(boolean enableFileLogging) {
        settings.setLogToFile(enableFileLogging);
    }

    public void setConsoleLogging(boolean enableConsoleLogging) {
        settings.setLogToConsole(enableConsoleLogging);
    }

    public boolean isLoadBalancing() {
        return settings.isServiceEnabled();
    }

    public boolean isConsoleLogging() {
        return settings.isLogToConsole();
    }

    public boolean isFileLogging() {
        return settings.isLogToFile();
    }

    public String getServiceFileName() {
        return settings.getServiceFileName();
    }

    public String getDeploymentFileName() {
        return settings.getDeploymentFileName();
    }

    public void setServiceFileName(String fileName) {
        settings.setServiceFileName(fileName);
    }

    public void setDeploymentFileName(String fileName) {
        settings.setDeploymentFileName(fileName);
    }
}

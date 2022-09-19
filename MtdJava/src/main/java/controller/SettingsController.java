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

import controller.yaml.IYaml;
import controller.yaml.YamlFactory;
import model.Settings;
import model.exception.InvalidFileNameException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SettingsController {
    private Settings settings = new Settings();

    /**
     * Loads a settings YAML file
     * @param file The settings YAML File
     * @throws IOException Throws if settings file could not be loaded
     */
    public void loadSettings(File file) throws IOException {
        IYaml<Settings> yaml = new YamlFactory<Settings>().createYaml(Settings.class);
        settings = yaml.load(file);
    }

    /**
     * Sets settings name and verifies that it is a valid file name, ie no special characters.
     * @param name The filename for the settings file including .yaml/.yml ending
     * @throws InvalidFileNameException Throws if illegal filename
     */
    public void setName(String name) throws InvalidFileNameException {
        String filename = name.strip();
        if (filename.matches("^[A-z\\d]+(\\.yaml|\\.yml)$")) {
            settings.setName(filename);
        }
        else {
            throw new InvalidFileNameException("File name must be alphanumeric and end with .yaml or .yml.");
        }
    }

    /**
     * Saves the Settings object to file in YAML format
     * @throws IOException Throws if file could not be saved
     */
    public void saveSettings() throws IOException {
        IYaml<Settings> yaml = new YamlFactory<Settings>().createYaml(Settings.class);
        String filename = settings.getName();
        yaml.save(new File(filename), settings);
    }

    /* Getters and Setters of the Settings object */

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

    public List<String> getDeploymentFileNames() {
        return settings.getDeploymentFileNames();
    }

    public void setServiceFileName(String fileName) {
        settings.setServiceFileName(fileName);
    }

    public void addDeploymentFilename(String fileName) {
        settings.getDeploymentFileNames().add(fileName);
    }
}

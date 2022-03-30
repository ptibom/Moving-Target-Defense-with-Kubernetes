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
import model.encapsulation.IPod;
import model.encapsulation.Pod;

import java.io.File;

public class SettingsController {
    private Settings settings = new Settings();

    public void getPod(String name) {

        try {
            IPod pod = new Pod(name, "default");

            pod.delete();

        } catch (Exception e) {
            System.out.println("Could not find the pod named: " + name);
        }
    }

    public Settings loadSettings(File file) {
        // todo load settings
        // settings = ...
        return settings;
    }

    public void saveSettings() {
        // todo save settings to file.
    }

    public void setConfigName(String name) {
        settings.setName(name);
    }

    public Settings getSettings() {
        return settings;
    }
}

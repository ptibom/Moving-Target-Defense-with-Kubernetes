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

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import controller.yaml.IYaml;
import controller.yaml.Yaml;
import controller.yaml.YamlFactory;
import model.Settings;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

public class TestSettingsController {
    @Test
    void tempTest() throws IOException {

        Settings settings = new Settings();
        // settings.setDeploymentFileName("TestDeployment.yaml");
        settings.setLogToConsole(true);
        settings.setLogToFile(false);
        settings.setServiceFileName("TestService.yaml");
        settings.setName("Settings.yaml");
    }

    @Test
    void tempTestLoad() throws IOException {
        YAMLMapper mapper = new YAMLMapper(new YAMLFactory());
        Settings settings = mapper.readValue(new File("Test4.yaml"), Settings.class);
        System.out.println(settings.getName());
    }
}

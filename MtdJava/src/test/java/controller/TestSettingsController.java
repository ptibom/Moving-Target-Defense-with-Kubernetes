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
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import model.Settings;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestSettingsController {
    @Test
    void tempTest() throws IOException {
        Settings settings = new Settings();
        settings.setDeploymentFileName("TestDeployment.yaml");
        settings.setLogToConsole(true);
        settings.setLogToFile(false);
        settings.setServiceFileName("TestService.yaml");
        settings.setName("Settings.yaml");

        YAMLFactory f = YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build();

        YAMLMapper mapper = new YAMLMapper(f);
        //YAMLMapper yamlMapper = new YAMLMapper();
        mapper.writeValue(new File("Test4.yaml"), settings);
        String result = mapper.writeValueAsString(settings);
        System.out.println(result);
    }
}

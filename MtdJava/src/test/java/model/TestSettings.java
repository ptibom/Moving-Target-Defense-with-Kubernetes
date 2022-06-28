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

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestSettings {

    @BeforeAll
    static void init() {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSetAndGetName() {
        Settings settings = new Settings();
        settings.setName("Test");
        assertEquals("Test", settings.getName());
    }

    @Test
    void testSetAndGetServiceEnabled() {
        Settings settings = new Settings();
        settings.setServiceEnabled(true);
        assertTrue(settings.isServiceEnabled());
    }

    @Test
    void testSetAndGetLogToConsole() {
        Settings settings = new Settings();
        settings.setLogToConsole(false);
        assertFalse(settings.isLogToConsole());
    }

    @Test
    void testSetAndGetLogToFile() {
        Settings settings = new Settings();
        settings.setLogToFile(true);
        assertTrue(settings.isLogToFile());
    }

    @Test
    void testSetAndGetServiceFileName() {
        Settings settings = new Settings();
        settings.setServiceFileName("Test.yaml");
        assertEquals("Test.yaml", settings.getServiceFileName());
    }

    @Test
    void testSetAndGetDeploymentFileName() {
        Settings settings = new Settings();
        settings.setDeploymentFileName("Test.yaml");
        assertEquals("Test.yaml", settings.getDeploymentFileName());
    }
}
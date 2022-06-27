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

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class TestMenuController {

    @BeforeAll
    static void init() {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testAskLoadBalancerQuestionFalse() {
        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        SettingsController settingsController = new SettingsController();
        assertFalse(menuController.askLoadBalancerQuestion(settingsController));
    }

    @Test
    void testAskLoadBalancerQuestionTrue() {
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        SettingsController settingsController = new SettingsController();
        assertTrue(menuController.askLoadBalancerQuestion(settingsController));
    }

    @Test
    void testAskFileLoggingQuestionFalse() {
        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);
        SettingsController settingsController = new SettingsController();
        MenuController menuController = new MenuController();
        menuController.askFileLoggingQuestion(settingsController);
        assertFalse(settingsController.isFileLogging());
    }

    @Test
    void testAskFileLoggingQuestionTrue() {
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        SettingsController settingsController = new SettingsController();
        MenuController menuController = new MenuController();
        menuController.askFileLoggingQuestion(settingsController);
        assertTrue(settingsController.isFileLogging());
    }

    @Test
    void testAskConsoleLoggingQuestionFalse() {
        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);
        SettingsController settingsController = new SettingsController();
        MenuController menuController = new MenuController();
        menuController.askConsoleLoggingQuestion(settingsController);
        assertFalse(settingsController.isConsoleLogging());
    }

    @Test
    void testAskConsoleLoggingQuestionTrue() {
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        SettingsController settingsController = new SettingsController();
        MenuController menuController = new MenuController();
        menuController.askConsoleLoggingQuestion(settingsController);
        assertTrue(settingsController.isConsoleLogging());
    }

    @Test
    void testAskServiceFileQuestionValid() {
        ByteArrayInputStream in = new ByteArrayInputStream("TestService.yaml".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        SettingsController settingsController = new SettingsController();
        menuController.askServiceFileQuestion(settingsController);
        assertEquals("TestService.yaml", settingsController.getServiceFileName());
    }

    @Test
    void testAskServiceFileQuestionInvalid() {
        ByteArrayInputStream in = new ByteArrayInputStream("Test.yaml".getBytes());
        System.setIn(in);
        assertThrows(NoSuchElementException.class, () -> {
            MenuController menuController = new MenuController();
            SettingsController settingsController = new SettingsController();
            menuController.askServiceFileQuestion(settingsController);
            settingsController.getServiceFileName();
        });
    }

    @Test
    void testAskDeploymentFileQuestionValid() {
        ByteArrayInputStream in = new ByteArrayInputStream("DeploymentPrintNode.yaml".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        SettingsController settingsController = new SettingsController();
        menuController.askDeploymentFileQuestion(settingsController);
        assertEquals("DeploymentPrintNode.yaml", settingsController.getDeploymentFileName());
    }

    @Test
    void testAskDeploymentFileQuestionInvalid() {
        ByteArrayInputStream in = new ByteArrayInputStream("Test.yaml".getBytes());
        System.setIn(in);
        assertThrows(NoSuchElementException.class, () -> {
            MenuController menuController = new MenuController();
            SettingsController settingsController = new SettingsController();
            menuController.askDeploymentFileQuestion(settingsController);
            settingsController.getServiceFileName();
        });
    }

    @Test
    void testInputFileNameValid() {
        ByteArrayInputStream in = new ByteArrayInputStream("DeploymentPrintNode.yaml".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        assertEquals("DeploymentPrintNode.yaml", menuController.inputFileName());
    }

    @Test
    void testInputFileNameInvalidFile() {
        ByteArrayInputStream in = new ByteArrayInputStream("Test.yaml".getBytes());
        System.setIn(in);
        assertThrows(NoSuchElementException.class, () -> {
            MenuController menuController = new MenuController();
            menuController.inputFileName();
        });
    }

    @Test
    void testInputFileNameInvalidName() {
        ByteArrayInputStream in = new ByteArrayInputStream("gh est.yaml\n!test.yaml\ntest.yaml.fail\nte st.yaml\nDeploymentPrintNode.yaml".getBytes());
        System.setIn(in);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        MenuController menuController = new MenuController();
        String result = menuController.inputFileName();
        Scanner sc = new Scanner(outputStream.toString());
        int counter = 0;
        while (sc.hasNextLine()) {
            assertEquals("Invalid input, try again:", sc.nextLine());
            counter++;
        }
        assertEquals(4, counter);
        assertEquals("DeploymentPrintNode.yaml", result);
    }

    @Test
    void testInputYesNoInvalid() {
        ByteArrayInputStream in = new ByteArrayInputStream("k".getBytes());
        System.setIn(in);
        assertThrows(NoSuchElementException.class, () -> {
            MenuController menuController = new MenuController();
            menuController.inputYesNo();
        });
        ByteArrayInputStream in2 = new ByteArrayInputStream("k\nn".getBytes());
        System.setIn(in2);
        MenuController menuController = new MenuController();
        assertFalse(menuController.inputYesNo());
    }

    @Test
    void testInputYesNoFalse() {
        ByteArrayInputStream in = new ByteArrayInputStream("n".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        assertFalse(menuController.inputYesNo());
    }

    @Test
    void testInputYesNoTrue() {
        ByteArrayInputStream in = new ByteArrayInputStream("y".getBytes());
        System.setIn(in);
        MenuController menuController = new MenuController();
        assertTrue(menuController.inputYesNo());
    }
}

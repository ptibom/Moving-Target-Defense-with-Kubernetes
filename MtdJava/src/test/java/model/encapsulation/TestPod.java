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

package model.encapsulation;

import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import model.encapsulation.exception.PodDeleteException;
import model.encapsulation.exception.PodLabelException;
import model.encapsulation.exception.PodNotFoundException;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestPod {
    @BeforeAll
    static void init() {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void testInitPod() throws PodNotFoundException {
        String podName = "nginx";
        String namespace = "default";
        IPod pod = new Pod(podName, namespace);
        assertEquals(podName, pod.getName());
    }

    @Test
    @Order(1)
    void testPodNotFound() {
        assertThrows(PodNotFoundException.class, () -> {
            IPod pod = new Pod("Fake name", "default");
        });
    }

    @Test
    @Order(1)
    void testGetPodLabels() throws PodNotFoundException {
        String podName = "nginx";
        IPod pod = new Pod(podName, "default");
        assertEquals(podName, pod.getLabels().get("run"));
    }

    @Test
    @Order(1)
    void testAddPodLabel() throws PodNotFoundException, PodLabelException {
        String labelKey = "mtd/test";
        String labelValue = "testpod";
        IPod pod = new Pod("nginx", "default");
        pod.addLabel(labelKey, labelValue);
        assertEquals(labelValue, pod.getLabels().get(labelKey));
    }

    @Test
    @Order(1)
    void testDeletePodLabel() throws PodNotFoundException, PodLabelException {
        String labelKey = "mtd/test";
        String labelValue = "testpod";
        IPod pod = new Pod("nginx", "default");
        pod.addLabel(labelKey, labelValue);
        assertEquals(labelValue, pod.getLabels().get(labelKey));
        pod.deleteLabel(labelKey);
        assertNull(pod.getLabels().get(labelKey));
    }

    // Only works if pod named "nginx" is created first.
    @Test
    @Order(2)
    void testDeletePod() throws PodDeleteException {
        try {
            IPod pod = new Pod("nginx", "default");
            pod.delete();
            Thread.sleep(5000);
            assertThrows(PodNotFoundException.class, () -> {
                IPod tmp = new Pod("nginx", "default");
            });
        } catch (PodNotFoundException e) {
            // Just pass the test if the pod "nginx" does not exist.
        } catch (InterruptedException e) {
            // Thread sleep exception.
        }
    }
}

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
import model.encapsulation.exception.NodeLabelException;
import model.encapsulation.exception.NodeNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestNode {

    @BeforeAll
    static void init() {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testInitNode() throws NodeNotFoundException {
        INode node = new Node("minikube");
        assertEquals("minikube", node.getName());
    }

    @Test
    void testNodeNotFound() {
        assertThrows(NodeNotFoundException.class, () -> {
            INode node = new Node("Fake name");
        });
    }

    @Test
    void testGetNodeLabels() throws NodeNotFoundException {
        INode node = new Node("minikube");
        assertEquals("linux", node.getLabels().get("kubernetes.io/os"));
    }

    @Test
    void testAddNodeLabel() throws NodeLabelException, NodeNotFoundException {
        String labelKey = "mtd/test";
        String labelValue = "testnode";
        INode node = new Node("minikube-m02");
        node.addLabel(labelKey, labelValue);
        assertEquals(labelValue, node.getLabels().get(labelKey));
    }

    @Test
    void testDeleteNodeLabel() throws NodeLabelException, NodeNotFoundException {
        String labelKey = "mtd/test";
        String labelValue = "testnode";
        INode node = new Node("minikube-m02");
        node.addLabel(labelKey, labelValue);
        assertEquals(labelValue, node.getLabels().get(labelKey));
        node.deleteLabel(labelKey);
        assertNull(node.getLabels().get(labelKey));
    }
}

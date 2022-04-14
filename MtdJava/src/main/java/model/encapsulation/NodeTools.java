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

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.util.generic.options.ListOptions;
import model.encapsulation.exception.NodeNotFoundException;

import java.util.LinkedList;
import java.util.List;

public class NodeTools {
    public static List<INode> getWorkerNodes() throws NodeNotFoundException {
        ListOptions listOptions = new ListOptions();
        listOptions.setLabelSelector("!node-role.kubernetes.io/master");
        try {
            List<V1Node> v1Nodes = Kubectl.get(V1Node.class)
                    .options(listOptions)
                    .execute();
            List<INode> nodeList = new LinkedList<>();
            for (V1Node v1Node : v1Nodes) {
                nodeList.add(new Node(v1Node));
            }
            return nodeList;
        } catch (KubectlException e) {
            throw new NodeNotFoundException();
        }
    }
}
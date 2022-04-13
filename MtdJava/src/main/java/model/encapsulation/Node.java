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
import model.encapsulation.exception.NodeLabelException;
import model.encapsulation.exception.NodeNotFoundException;

import java.util.Map;

public class Node implements INode {
    private V1Node v1Node;

    public Node(String name) throws NodeNotFoundException {
        try {
            v1Node = Kubectl.get(V1Node.class)
                    .name(name)
                    .execute();
        } catch (KubectlException e) {
            throw new NodeNotFoundException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return v1Node.getMetadata().getName();
    }

    @Override
    public Map<String, String> getLabels() {
        return v1Node.getMetadata().getLabels();
    }

    @Override
    public void addLabel(String key, String value) throws NodeLabelException {
        try {
            v1Node = Kubectl.label(V1Node.class)
                    .addLabel(key, value)
                    .name(v1Node.getMetadata().getName())
                    .execute();
        } catch (KubectlException e) {
            throw new NodeLabelException(e.getMessage());
        }
    }

    @Override
    public void deleteLabel(String key) throws NodeLabelException {
        addLabel(key, null);
    }

}

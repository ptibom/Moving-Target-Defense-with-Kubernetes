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

import java.util.List;

public class Node implements INode {

    V1Node v1Node;

    public Node(String name) {
        try {
            v1Node = Kubectl.get(V1Node.class)
                    .name(name)
                    .execute();
        } catch (KubectlException e) {
            // Todo throw a custom exception
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return v1Node.getMetadata().getName();
    }

    @Override
    public List<String> getLabels() {
        // todo
        return null;
    }

    @Override
    public void addLabel(String name) {
        // todo
    }

    @Override
    public void delete() throws Exception {
        // todo
    }
}

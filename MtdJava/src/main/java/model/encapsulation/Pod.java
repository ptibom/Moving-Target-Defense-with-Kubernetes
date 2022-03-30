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
import io.kubernetes.client.openapi.models.V1Pod;

import java.io.File;
import java.util.List;

public class Pod implements IPod {
    private V1Pod v1Pod;

    public Pod(String podName, String namespace) throws Exception {
        try {
            v1Pod = Kubectl.get(V1Pod.class)
                    .namespace(namespace)
                    .name(podName)
                    .execute();
        } catch (KubectlException e) {
            // TODO add custom exception.
            throw new Exception();
        }
    }

    public Pod(File yamlFile, String namespace) {
        // Todo (maybe not a namespace)
    }

    @Override
    public String podName() {
        return v1Pod.getMetadata().getName();
    }

    /**
     * Gets a list of labels attached to the pod.
     * @return Returns a list of strings.
     */
    @Override
    public List<String> getLabels() {
        // Todo
        return null;
    }

    @Override
    public void addLabel(String name) {
        // Todo
    }

    /**
     * Hejsvejs
     * @throws Exception
     */
    @Override
    public void delete() throws Exception {
        try {
            Kubectl.delete(V1Pod.class)
                    .namespace("default")
                    .name(v1Pod.getMetadata().getName())
                    .execute();
        } catch (KubectlException e) {
            throw new Exception();
        }
    }
}

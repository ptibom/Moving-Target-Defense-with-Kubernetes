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
import io.kubernetes.client.proto.V1;
import io.kubernetes.client.util.Yaml;
import model.encapsulation.exception.ApplyException;
import model.encapsulation.exception.PodDeleteException;
import model.encapsulation.exception.PodLabelException;
import model.encapsulation.exception.PodNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Pod implements IPod {
    private V1Pod v1Pod;

    public Pod(String podName, String namespace) throws PodNotFoundException {
        try {
            v1Pod = Kubectl.get(V1Pod.class)
                    .namespace(namespace)
                    .name(podName)
                    .execute();
        } catch (KubectlException e) {
            throw new PodNotFoundException(e.getMessage());
        }
    }

    public Pod(V1Pod pod) {
        v1Pod = pod;
    }

    public Pod(File yamlFile) throws IOException {
        v1Pod = (V1Pod) Yaml.load(yamlFile);
    }

    @Override
    public void apply(String namespace) throws ApplyException {
        try {
            v1Pod = Kubectl.create(V1Pod.class)
                    .namespace(namespace)
                    .resource(v1Pod)
                    .execute();
        } catch (KubectlException e) {
            throw new ApplyException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return v1Pod.getMetadata().getName();
    }

    @Override
    public String getNodeName() {
        return v1Pod.getSpec().getNodeName();
    }

    /**
     * Gets a list of labels attached to the pod.
     * @return Returns a list of strings.
     */
    @Override
    public Map<String, String> getLabels() {
        return v1Pod.getMetadata().getLabels();
    }

    @Override
    public void addLabel(String key, String value) throws PodLabelException {
        try {
            v1Pod = Kubectl.label(V1Pod.class)
                    .addLabel(key, value)
                    .namespace(v1Pod.getMetadata().getNamespace())
                    .name(v1Pod.getMetadata().getName())
                    .execute();
        } catch (KubectlException e) {
            throw new PodLabelException(e.getMessage());
        }
    }

    @Override
    public void deleteLabel(String key) throws PodLabelException {
        addLabel(key, null);
    }

    @Override
    public void delete() throws PodDeleteException {
        try {
            Kubectl.delete(V1Pod.class)
                    .namespace(v1Pod.getMetadata().getNamespace())
                    .name(v1Pod.getMetadata().getName())
                    .execute();
            v1Pod = null;
        } catch (KubectlException e) {
            throw new PodDeleteException(e.getMessage());
        }
    }
}

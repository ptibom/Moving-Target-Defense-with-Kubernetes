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

package model.kubernetes;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Yaml;
import model.kubernetes.exception.ApplyException;
import model.kubernetes.exception.PodDeleteException;
import model.kubernetes.exception.PodLabelException;
import model.kubernetes.exception.PodNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Pod implements IPod {
    private V1Pod v1Pod;

    /**
     * Gets pod object from the cluster by pod name and namspeace
     * @param podName The pod name
     * @param namespace The namespace
     * @throws PodNotFoundException Throws if pod is not found
     */
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

    /**
     * Gets pod object from the cluster by pod name and the default namespace
     * @param podName the pod name
     * @throws PodNotFoundException Throws if pod is not found
     */
    public Pod(String podName) throws PodNotFoundException {
        this(podName, "default");
    }

    /**
     * Creates a Pod object from a Kubernetes client V1Pod
     * @param pod
     */
    public Pod(V1Pod pod) {
        v1Pod = pod;
    }

    /**
     * Creates a Pod object from a YAML file
     * @param yamlFile The YAML File
     * @throws IOException Throws if file not found
     */
    public Pod(File yamlFile) throws IOException {
        v1Pod = (V1Pod) Yaml.load(yamlFile);
    }

    /**
     * Apply the pod to the cluster
     * @param namespace The namespace where the pod should run
     * @throws ApplyException Throws if apply failed in the cluster
     */
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

    /**
     * Gets the name of the pod
     * @return The pod name
     */
    @Override
    public String getName() {
        return v1Pod.getMetadata().getName();
    }

    /**
     * Gets the name of the Node the pod is running on
     * @return The name of the node
     */
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

    /**
     * Adds a label to the pod
     * @param key The label key
     * @param value The label value
     * @throws PodLabelException Throws if label could not be added
     */
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

    /**
     * Delete label from the pod
     * @param key Key of the label to delete
     * @throws PodLabelException
     */
    @Override
    public void deleteLabel(String key) throws PodLabelException {
        addLabel(key, null);
    }

    /**
     * Gets the last known phase of the pod
     * See Pod Lifecycle on kubernetes docs
     * @return The phase. running, pending, etc
     */
    @Override
    public String getPhase() {
        return v1Pod.getStatus().getPhase();
    }

    /**
     * Deletes the pod from the cluster
     * @throws PodDeleteException Throws if pod could not be deleted
     */
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

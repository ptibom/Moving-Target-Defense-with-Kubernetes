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
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.generic.options.ListOptions;
import model.kubernetes.exception.NodeLabelException;
import model.kubernetes.exception.NodeNotFoundException;
import model.kubernetes.exception.PodNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Node implements INode {
    private V1Node v1Node;
    private String namespace = "default";

    /**
     * Gets a node from the cluster by its node name
     * @param name The node name
     * @throws NodeNotFoundException Throws exception if node is not found
     */
    public Node(String name) throws NodeNotFoundException {
        try {
            v1Node = Kubectl.get(V1Node.class)
                    .name(name)
                    .execute();
        } catch (KubectlException e) {
            throw new NodeNotFoundException(e.getMessage());
        }
    }

    /**
     * Gets a node from the cluster by its node name and namespace
     * @param name The node name
     * @param namespace The namespace
     * @throws NodeNotFoundException Throws exception if node is not found
     */
    public Node(String name, String namespace) throws NodeNotFoundException {
        this(name);
        this.namespace = namespace;
    }

    /**
     * Creates a Node object
     * @param v1Node Kubernetes Client V1Node
     */
    public Node(V1Node v1Node) {
        this.v1Node = v1Node;
    }

    /**
     * Creates a Node object
     * @param v1Node Kubernetes Client V1Node
     * @param namespace The namespace
     */
    public Node(V1Node v1Node, String namespace) {
        this(v1Node);
        this.namespace = namespace;
    }

    /**
     * Gets this node name
     * @return The node name
     */
    @Override
    public String getName() {
        return v1Node.getMetadata().getName();
    }

    /**
     * Gets all the labels on this node
     * @return Map of labels
     */
    @Override
    public Map<String, String> getLabels() {
        return v1Node.getMetadata().getLabels();
    }

    /**
     * Adds a label to this node
     * @param key Label key
     * @param value Label value
     * @throws NodeLabelException Throws if label could not be added
     */
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

    /**
     * Get all pods on this node
     * @return List of pods on this node
     * @throws PodNotFoundException Throws if no pods found on this node
     */
    @Override
    public List<IPod> getPods() throws PodNotFoundException {
        try {
            String fieldSelector = "spec.nodeName=" + v1Node.getMetadata().getName();
            ListOptions listOptions = new ListOptions();
            listOptions.setFieldSelector(fieldSelector);
            List<V1Pod> v1PodList = Kubectl.get(V1Pod.class)
                    .options(listOptions)
                    .namespace(namespace)
                    .execute();
            List<IPod> podList = new ArrayList<>();
            for (V1Pod tmp : v1PodList) {
                podList.add(new Pod(tmp));
            }
            return podList;
        } catch (KubectlException e) {
            throw new PodNotFoundException(e.getMessage());
        }
    }

    /**
     * Deletes a label from this node
     * @param key Key of the label to delete
     * @throws NodeLabelException Throws if label could not be deleted
     */
    @Override
    public void deleteLabel(String key) throws NodeLabelException {
        addLabel(key, null);
    }

}

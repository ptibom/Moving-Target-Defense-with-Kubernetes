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

import model.kubernetes.exception.ApplyException;
import model.kubernetes.exception.PodDeleteException;
import model.kubernetes.exception.PodLabelException;

import java.util.Map;

public interface IPod {
    /**
     * Gets the name of the pod
     * @return The pod name
     */
    String getName();

    /**
     * Gets the name of the Node the pod is running on
     * @return The name of the node
     */
    String getNodeName();

    /**
     * Gets a list of labels attached to the pod.
     * @return Returns a list of strings.
     */
    Map<String, String> getLabels();

    /**
     * Adds a label to the pod
     * @param key The label key
     * @param value The label value
     * @throws PodLabelException Throws if label could not be added
     */
    void addLabel(String key, String value) throws PodLabelException;

    /**
     * Apply the pod to the cluster
     * @param namespace The namespace where the pod should run
     * @throws ApplyException Throws if apply failed in the cluster
     */
    void apply(String namespace) throws ApplyException;

    /**
     * Delete label from the pod
     * @param key Key of the label to delete
     * @throws PodLabelException
     */
    void deleteLabel(String key) throws PodLabelException;

    /**
     * Gets the last known phase of the pod
     * See Pod Lifecycle on kubernetes docs
     * @return The phase. running, pending, etc
     */
    String getPhase();

    /**
     * Deletes the pod from the cluster
     * @throws PodDeleteException Throws if pod could not be deleted
     */
    void delete() throws PodDeleteException;
}

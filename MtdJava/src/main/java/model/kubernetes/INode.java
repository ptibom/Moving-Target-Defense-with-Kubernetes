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

import model.kubernetes.exception.NodeLabelException;
import model.kubernetes.exception.PodNotFoundException;

import java.util.List;
import java.util.Map;

public interface INode {

    /**
     * Gets this node name
     * @return The node name
     */
    String getName();

    /**
     * Gets all the labels on this node
     * @return Map of labels
     */
    Map<String, String> getLabels();

    /**
     * Adds a label to this node
     * @param key Label key
     * @param value Label value
     * @throws NodeLabelException Throws if label could not be added
     */
    void addLabel(String key, String value) throws NodeLabelException;

    /**
     * Get all pods on this node
     * @return List of pods on this node
     * @throws PodNotFoundException Throws if no pods found on this node
     */
    List<IPod> getPods() throws PodNotFoundException;

    /**
     * Deletes a label from this node
     * @param key Key of the label to delete
     * @throws NodeLabelException Throws if label could not be deleted
     */
    void deleteLabel(String key) throws NodeLabelException;
}
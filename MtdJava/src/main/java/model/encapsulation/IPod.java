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

import model.encapsulation.exception.ApplyException;
import model.encapsulation.exception.PodDeleteException;
import model.encapsulation.exception.PodLabelException;

import java.util.Map;

public interface IPod {
    String getName();
    String getNodeName();
    Map<String, String> getLabels();
    void addLabel(String key, String value) throws PodLabelException;
    void apply(String namespace) throws ApplyException;
    void deleteLabel(String key) throws PodLabelException;
    String getPhase();
    void delete() throws PodDeleteException;
}

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
import model.encapsulation.exception.DeploymentDeleteException;
import model.encapsulation.exception.DeploymentNotFoundException;

import java.util.List;

public interface IDeployment {

    void apply() throws ApplyException;
    void rolloutRestart() throws ApplyException;
    List<IPod> getPods() throws DeploymentNotFoundException;
    void delete() throws DeploymentDeleteException;
    String getName();
}

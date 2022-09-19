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

package controller.algs;

import model.kubernetes.IDeployment;

import java.util.List;

public interface IMtdAlg {

    /**
     * Used for running the MTD algorithm forever
     * @return Returns a list of logging events
     */
    List<String> run();

    /**
     * Used for running the MTD algorithm for a number of swaps, then it cancels.
     * @param nSwaps The number of times the algorithm should swap before it cancels.
     * @return Returns a list of logging events
     */
    List<String> run(int nSwaps);
    void setTimeBetweenSwap(int milliseconds);
    int getTimeBetweenSwap();
}
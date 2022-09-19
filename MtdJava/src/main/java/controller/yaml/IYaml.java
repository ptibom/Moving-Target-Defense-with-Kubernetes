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

package controller.yaml;

import java.io.File;
import java.io.IOException;

public interface IYaml<T> {
    /**
     * Load a generic YAMl file to POJO
     * @param file YAML file to load
     * @return Returns POJO
     * @throws IOException Throws exception if file could not be read
     */
    T load(File file) throws IOException;

    /**
     * Saves generic class to YAML file on the disk
     * @param file The file to be stored
     * @param t Generic class
     * @throws IOException
     */
    void save(File file, T t) throws IOException;
}

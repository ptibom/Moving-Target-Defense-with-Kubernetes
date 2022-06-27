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

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import model.Settings;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class Yaml<T> implements IYaml<T> {

    Class<T> clazz;

    public Yaml(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T load(File file) throws IOException {
        YAMLMapper mapper = new YAMLMapper(new YAMLFactory());
        T t = mapper.readValue(file, clazz);
        return t;
    }

    @Override
    public void save(File file, T t) throws IOException {
        YAMLFactory f = YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .build();
        YAMLMapper mapper = new YAMLMapper(f);
        mapper.writeValue(file, t);
    }
}

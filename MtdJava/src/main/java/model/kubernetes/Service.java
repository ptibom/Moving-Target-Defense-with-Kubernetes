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
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;
import model.kubernetes.exception.ApplyException;
import model.kubernetes.exception.KubeServiceDeleteException;
import model.kubernetes.exception.KubeServiceNotFoundException;

import java.io.File;
import java.io.IOException;

public class Service implements IService {
    private V1Service v1Service;

    /**
     * Gets a Service from the cluster by service name and namespace
     * @param name The service name
     * @param namespace The namespace
     * @throws KubeServiceNotFoundException Throws if service could not be found
     */
    public Service(String name, String namespace) throws KubeServiceNotFoundException {
        try {
            v1Service = Kubectl.get(V1Service.class)
                    .name(name)
                    .namespace(namespace)
                    .execute();
        } catch (KubectlException e) {
            throw new KubeServiceNotFoundException(e.getMessage());
        }
    }

    /**
     * Creates a service object from a kubernetes Service YAML file
     * @param file A Service.yaml file
     * @throws IOException Throws if file could not be found
     */
    public Service(File file) throws IOException {
        v1Service = (V1Service) Yaml.load(file);
    }

    /**
     * Applies the service to the cluster
     * @throws ApplyException Throws if it could not be applied
     */
    @Override
    public void apply() throws ApplyException {
        try {
            v1Service = Kubectl.apply(V1Service.class)
                    .resource(v1Service)
                    .execute();
        } catch (KubectlException e) {
            throw new ApplyException(e.getMessage());
        }
    }


    /**
     * Deletes the service from the cluster
     * @throws KubeServiceDeleteException Throws if it could not be deleted
     */
    @Override
    public void delete() throws KubeServiceDeleteException {
        try {
            Kubectl.delete(V1Service.class)
                    .namespace(v1Service.getMetadata().getNamespace())
                    .name(v1Service.getMetadata().getName())
                    .execute();
        } catch (KubectlException e) {
            throw new KubeServiceDeleteException(e.getMessage());
        }
        v1Service = null;
    }
}

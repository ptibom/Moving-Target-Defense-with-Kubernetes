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

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Yaml;
import model.kubernetes.exception.ApplyException;
import model.kubernetes.exception.DeploymentDeleteException;
import model.kubernetes.exception.DeploymentNotFoundException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Deployment implements IDeployment {

    private V1Deployment v1Deployment;
    private String filename;

    public Deployment(File file) throws IOException {
        filename = file.getName();
        v1Deployment = (V1Deployment) Yaml.load(file);
    }

    public Deployment(String name, String namespace) throws DeploymentNotFoundException {
        try {
            v1Deployment = Kubectl.get(V1Deployment.class)
                    .namespace(namespace)
                    .name(name)
                    .execute();
        } catch (KubectlException e) {
            throw new DeploymentNotFoundException(e.getMessage());
        }
    }

    public List<IPod> getPods() throws DeploymentNotFoundException {
        CoreV1Api api = new CoreV1Api();
        String label = "app=" + v1Deployment.getMetadata().getLabels().get("app");
        try {
            V1PodList list = api.listNamespacedPod(v1Deployment.getMetadata().getNamespace(),
                    null, null, null, null, label, null, null, null, null, null);
            List<IPod> pods = new ArrayList<>();
            for(V1Pod pod : list.getItems()) {
                pods.add(new Pod(pod));
            }
            return pods;
        } catch (ApiException e) {
            throw new DeploymentNotFoundException(e.getMessage());
        }
    }

    @Override
    public void apply() throws ApplyException {
        try {
            v1Deployment = Kubectl.apply(V1Deployment.class)
                    .resource(v1Deployment)
                    .fieldManager(null)
                    .execute();
        } catch (KubectlException e) {
            throw new ApplyException(e.getMessage());
        }
    }

    @Override
    public void rolloutRestart() throws ApplyException {
        Map<String, String> restart = new HashMap<>();
        restart.put("date", Instant.now().getEpochSecond() + "");
        v1Deployment.getSpec().getTemplate().getMetadata().setAnnotations(restart);
        try {
            v1Deployment = Kubectl.patch(V1Deployment.class)
                            .namespace(v1Deployment.getMetadata().getNamespace())
                            .name(v1Deployment.getMetadata().getName())
                            .patchType(V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH)
                            .patchContent(new V1Patch("{\"spec\":{\"template\":{\"metadata\":{\"annotations\":{\"date\":\"" +
                                System.currentTimeMillis() +"\"}}}}}"))
                            .execute();
        } catch (KubectlException e) {
            throw new ApplyException(e.getMessage());
        }
    }

    @Override
    public void delete() throws DeploymentDeleteException {
        try {
            Kubectl.delete(V1Deployment.class)
                    .namespace(v1Deployment.getMetadata().getNamespace())
                    .name(v1Deployment.getMetadata().getName())
                    .execute();
            v1Deployment = null;
        } catch (KubectlException e) {
            throw new DeploymentDeleteException(e.getMessage());
        }
    }

    @Override
    public String getName() {
        return v1Deployment.getMetadata().getName();
    }

    @Override
    public String getFileName() {
        return filename;
    }
}

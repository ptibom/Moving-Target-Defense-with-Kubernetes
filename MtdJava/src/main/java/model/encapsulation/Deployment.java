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

import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.Yaml;
import model.encapsulation.exception.ApplyException;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Deployment implements IDeployment {

    private V1Deployment v1Deployment;

    public Deployment(File file) throws IOException {
        this.v1Deployment = (V1Deployment) Yaml.load(file);
    }

    @Override
    public void apply() throws ApplyException {
        try {
            Kubectl.apply(V1Deployment.class)
                    .resource(v1Deployment)
                    .execute();
        } catch (KubectlException e) {
            throw new ApplyException(e.getMessage());
        }
    }

    @Override
    public void rolloutRestart() {
        Map<String, String> restart = new HashMap<>();
        restart.put("date", Instant.now().getEpochSecond() + "");
        v1Deployment.getSpec().getTemplate().getMetadata().setAnnotations(restart);
        Kubectl.patch(V1Deployment.class)
                .name(v1Deployment.getMetadata().getName())
                .patchType(V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH)
                .patchContent(new V1Patch("{\"spec\":{\"template\":{\"metadata\":{\"annotations\"}}"))
                .execute();
    }

    @Override
    public void replace() {

    }

    @Override
    public void delete() {

    }
}

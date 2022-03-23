import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.models.V1Node;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JavaTest {
    public static void main(String[] args) throws IOException, ApiException, KubectlException {

        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);

        for(int i=0; i < 6; i++){
            V1Pod creatingPod = (V1Pod) Yaml.load(new File("nginx.yaml"));
            creatingPod.getMetadata().setName(String.format("nginx%d", i));
            V1Pod createdPod = Kubectl.create(V1Pod.class)
                    .resource(creatingPod)
                    .execute();
        }


        V1Pod creatingPod = (V1Pod) Yaml.load(new File("nginx.yaml"));
        creatingPod.getMetadata().setName(String.format("nginx%d", 1));
        V1Pod createdPod = Kubectl.create(V1Pod.class)
                .resource(creatingPod)
                .execute();

        List<V1Pod> pods = Kubectl.get(V1Pod.class)
                .namespace("default")
                .execute();

        List<V1Node> nodes = Kubectl.get(V1Node.class)
                .execute();


        for (V1Pod pod : pods){
            Kubectl.delete(V1Pod.class)
                    .namespace("default")
                    .name(pod.getMetadata().getName())
                    .execute();
        }

        for (V1Pod item : pods) {
            System.out.println(item.getMetadata().getName());
            System.out.println(item.getSpec().getNodeName());
        }

        int labelCounter = 0;
        for (V1Node node : nodes){
            Kubectl.label(V1Node.class)
                    .addLabel("mtd/node", String.format("node%d", labelCounter))
                    .name(node.getMetadata().getName())
                    .execute();
            labelCounter++;
        }

        nodes = Kubectl.get(V1Node.class)
                .execute();

        for (V1Node node : nodes) {
            System.out.println(node.getMetadata().getLabels());
        }
    }
}
//DEPS org.apache.camel:camel-bom:4.10.2@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.torchserve.TorchServeConstants;
import org.apache.camel.component.torchserve.client.model.ScaleWorkerOptions;

public class register_model extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:register?repeatCount=1")
            .to("torchserve:management/register?url=https://torchserve.pytorch.org/mar_files/mnist_v2.mar")
            .log("Status: ${body}")
            .to("direct:scale-worker");

        // Set up workers for the model after registration to make it available for inference
        from("direct:scale-worker")
            .setHeader(TorchServeConstants.SCALE_WORKER_OPTIONS,
                constant(ScaleWorkerOptions.builder().minWorker(1).maxWorker(2).build()))
            .to("torchserve:management/scale-worker?modelName=mnist_v2")
            .log("Status: ${body}");
        // @formatter:on
    }
}

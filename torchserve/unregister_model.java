// camel-k: language=java dependency=camel:torchserve

import org.apache.camel.builder.RouteBuilder;

public class unregister_model extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:unregister?repeatCount=1")
            .to("torchserve:management/unregister?modelName=mnist_v2")
            .log("Status: ${body}");
        // @formatter:on
    }
}

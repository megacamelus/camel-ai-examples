//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

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

// camel-k: language=java dependency=camel:torchserve

//DEPS org.apache.camel:camel-bom:4.9.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

import org.apache.camel.builder.RouteBuilder;

public class describe_model extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:describe?repeatCount=1")
            .to("torchserve:management/describe?modelName=mnist_v2")
            .log("${body[0]}");
        // @formatter:on
    }
}

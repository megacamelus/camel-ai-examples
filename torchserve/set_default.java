//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

import org.apache.camel.builder.RouteBuilder;

public class set_default extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:set-default?repeatCount=1")
            .to("torchserve:management/set-default?modelName=mnist_v2&modelVersion=2.0")
            .log("Status: ${body}");
        // @formatter:on
    }
}

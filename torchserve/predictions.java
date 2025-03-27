//DEPS org.apache.camel:camel-bom:4.10.2@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

import org.apache.camel.builder.RouteBuilder;

public class predictions extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("file:data/mnist?noop=true&recursive=true&include=.*\\.png")
            .to("torchserve:inference/predictions?modelName=mnist_v2")
            .log("${headers.camelFileName} => ${body}");
        // @formatter:on
    }
}

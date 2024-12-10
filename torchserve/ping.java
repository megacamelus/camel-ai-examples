// camel-k: language=java dependency=camel:torchserve

//DEPS org.apache.camel:camel-bom:4.9.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

import org.apache.camel.builder.RouteBuilder;

public class ping extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:ping?period=10000")
            .to("torchserve:inference/ping")
            .log("Status: ${body}");
        // @formatter:on
    }
}

//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-torchserve

import org.apache.camel.builder.RouteBuilder;

public class metrics extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:metrics?period=10000")
            .to("torchserve:metrics/metrics?metricsName=MemoryUsed")
            .log("${body}");
        // @formatter:on
    }
}

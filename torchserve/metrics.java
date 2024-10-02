// camel-k: language=java dependency=camel:torchserve

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

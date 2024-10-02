// camel-k: language=java dependency=camel:torchserve

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

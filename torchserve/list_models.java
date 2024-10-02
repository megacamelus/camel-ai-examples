// camel-k: language=java dependency=camel:torchserve

import org.apache.camel.builder.RouteBuilder;

public class list_models extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:list?repeatCount=1")
            .to("torchserve:management/list")
            .log("${body.models}");
        // @formatter:on
    }
}

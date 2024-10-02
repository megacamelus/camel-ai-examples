// camel-k: language=java dependency=camel:torchserve

import org.apache.camel.builder.RouteBuilder;

public class predictions extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("file:data/objects?noop=true&recursive=true&include=.*\\.(jpg|png)")
            .to("torchserve:inference/predictions?modelName=squeezenet1_1")
            .log("${headers.camelFileName} => ${body}");
        // @formatter:on
    }
}

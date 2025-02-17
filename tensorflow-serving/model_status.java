//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-tensorflow-serving

import org.apache.camel.builder.RouteBuilder;

public class model_status extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:model-status?repeatCount=1")
            .to("tensorflow-serving:model-status?modelName=mnist&modelVersion=1")
            .log("Status: ${body.getModelVersionStatus(0).state}");
        // @formatter:on
    }
}

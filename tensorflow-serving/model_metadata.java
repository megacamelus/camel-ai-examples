//DEPS org.apache.camel:camel-bom:4.10.0-SNAPSHOT@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-tensorflow-serving

import org.apache.camel.builder.RouteBuilder;

public class model_metadata extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:model-metadata?repeatCount=1")
            .to("tensorflow-serving:model-metadata?modelName=mnist&modelVersion=1")
            .log("Metadata: ${body.getMetadataOrThrow('signature_def')}");
        // @formatter:on
    }
}

// camel-k: language=java dependency=ai.djl.pytorch:pytorch-model-zoo:0.29.0 dependency=ai.djl.pytorch:pytorch-engine:0.29.0

//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-djl

import org.apache.camel.builder.RouteBuilder;

public class ssd extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("file:data/objects?noop=true&recursive=true&include=.*\\.(jpg|png)")
            .to("djl:cv/object_detection?artifactId=ssd")
            .log("${headers.camelFileName} => ${body}");
        // @formatter:on
    }
}

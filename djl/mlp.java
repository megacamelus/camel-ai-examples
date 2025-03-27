// camel-k: language=java dependency=ai.djl:model-zoo:0.29.0 dependency=ai.djl.pytorch:pytorch-engine:0.29.0

//DEPS org.apache.camel:camel-bom:4.10.2@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-djl

import java.util.Random;

import org.apache.camel.builder.RouteBuilder;

public class mlp extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        var num = new Random(System.currentTimeMillis()).nextInt(10);
        // @formatter:off
        fromF("file:data/mnist/%s?noop=true&recursive=true&include=.*\\.(jpg|png)", num)
            .to("djl:cv/image_classification?artifactId=mlp")
            .log("${headers.camelFileName} => ${body.best.className}");
        // @formatter:on
    }
}

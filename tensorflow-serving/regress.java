// camel-k: language=java dependency=camel:tensorflow-serving

//DEPS org.apache.camel:camel-bom:4.10.0-SNAPSHOT@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-tensorflow-serving

import org.apache.camel.builder.RouteBuilder;
import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.Features;
import org.tensorflow.example.FloatList;

import tensorflow.serving.InputOuterClass.ExampleList;
import tensorflow.serving.InputOuterClass.Input;

public class regress extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:regress?repeatCount=1")
            .setBody(constant(Input.newBuilder()
                .setExampleList(ExampleList.newBuilder()
                    .addExamples(Example.newBuilder()
                        .setFeatures(Features.newBuilder()
                            .putFeature("x", Feature.newBuilder()
                                .setFloatList(FloatList.newBuilder().addValue(1.0f))
                                .build()))))
                .build()))
            .to("tensorflow-serving:regress?modelName=half_plus_two&modelVersion=123&signatureName=regress_x_to_y")
            .log("Result: ${body.result}");
        // @formatter:on
    }
}

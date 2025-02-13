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

public class classify extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:classify?repeatCount=1")
            .setBody(constant(createInput("x", 1.0f)))
            .to("tensorflow-serving:classify?modelName=half_plus_two&modelVersion=123&signatureName=classify_x_to_y")
            .log("Result: ${body.result}");
        // @formatter:on
    }

    Input createInput(String key, float f) {
        Feature feature = Feature.newBuilder()
                .setFloatList(FloatList.newBuilder().addValue(f))
                .build();
        Features features = Features.newBuilder()
                .putFeature(key, feature)
                .build();
        Example example = Example.newBuilder()
                .setFeatures(features)
                .build();
        ExampleList exampleList = ExampleList.newBuilder()
                .addExamples(example)
                .build();
        return Input.newBuilder()
                .setExampleList(exampleList)
                .build();
    }
}

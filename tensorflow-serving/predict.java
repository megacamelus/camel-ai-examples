// camel-k: language=java dependency=camel:tensorflow-serving

//DEPS org.apache.camel:camel-bom:4.10.0-SNAPSHOT@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-tensorflow-serving

import org.apache.camel.builder.RouteBuilder;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.TensorShapeProto.Dim;

import tensorflow.serving.Predict.PredictRequest;

public class predict extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:predict?repeatCount=1")
            .setBody(constant(PredictRequest.newBuilder()
                .putInputs("x", TensorProto.newBuilder()
                    .setDtype(DataType.DT_FLOAT)
                    .setTensorShape(TensorShapeProto.newBuilder()
                        .addDim(Dim.newBuilder().setSize(3)))
                    .addFloatVal(1.0f)
                    .addFloatVal(2.0f)
                    .addFloatVal(5.0f)
                    .build())
                .build()))
            .to("tensorflow-serving:predict?modelName=half_plus_two&modelVersion=123")
            .log("Result: ${body.getOutputsOrThrow('y')}");
        // @formatter:on
    }
}

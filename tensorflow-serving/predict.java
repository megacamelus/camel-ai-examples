//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-tensorflow-serving

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import org.tensorflow.framework.TensorShapeProto.Dim;

import com.google.protobuf.Int64Value;

import tensorflow.serving.Model.ModelSpec;
import tensorflow.serving.Predict.PredictRequest;
import tensorflow.serving.Predict.PredictResponse;

public class predict extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("file:data/mnist?noop=true&recursive=true&include=.*\\.png")
            .process(this::toPredictRequest)
            .to("tensorflow-serving:predict?modelName=mnist&modelVersion=1")
            .process(this::argmax)
            .log("${headers.camelFileName} => ${body}");
        // @formatter:on
    }

    void toPredictRequest(Exchange exchange) {
        byte[] body = exchange.getMessage().getBody(byte[].class);
        List<Float> data = preprocess(body);
        TensorProto inputs = TensorProto.newBuilder()
                .setDtype(DataType.DT_FLOAT)
                .setTensorShape(TensorShapeProto.newBuilder()
                        .addDim(Dim.newBuilder().setSize(28))
                        .addDim(Dim.newBuilder().setSize(28)))
                .addAllFloatVal(data)
                .build();
        PredictRequest request = PredictRequest.newBuilder()
                .setModelSpec(ModelSpec.newBuilder()
                        .setName("mnist")
                        .setVersion(Int64Value.of(1)))
                .putInputs("keras_tensor", inputs)
                .build();
        exchange.getMessage().setBody(request);
    }

    List<Float> preprocess(byte[] data) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            int width = image.getWidth();
            int height = image.getHeight();
            if (width != 28 || height != 28) {
                throw new RuntimeCamelException("Image size must be 28x28");
            }
            List<Float> normalised = new ArrayList<>(width * height);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    normalised.add((rgb & 0xFF) / 255.0f);
                }
            }
            return normalised;
        } catch (IOException e) {
            throw new RuntimeCamelException("Error reading image", e);
        }
    }

    void argmax(Exchange exchange) {
        PredictResponse response = exchange.getMessage().getBody(PredictResponse.class);
        TensorProto tensor = response.getOutputsOrThrow("output_0");
        int result = IntStream.range(0, tensor.getFloatValCount())
                .reduce((max, i) -> tensor.getFloatVal(max) > tensor.getFloatVal(i) ? max : i)
                .orElseThrow();
        exchange.getMessage().setBody(result);
    }
}

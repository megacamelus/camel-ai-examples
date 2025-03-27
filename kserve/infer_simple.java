//DEPS org.apache.camel:camel-bom:4.10.2@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-kserve

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import com.google.protobuf.ByteString;

import inference.GrpcPredictV2.InferTensorContents;
import inference.GrpcPredictV2.ModelInferRequest;
import inference.GrpcPredictV2.ModelInferResponse;

public class infer_simple extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:infer-simple?repeatCount=1")
            .setBody(constant(createRequest()))
            .to("kserve:infer?modelName=simple&modelVersion=1")
            .process(this::postprocess)
            .log("Result[0]: ${body[0]}")
            .log("Result[1]: ${body[1]}");
        // @formatter:on
    }

    ModelInferRequest createRequest() {
        var ints0 = IntStream.range(1, 17).boxed().collect(Collectors.toList());
        var content0 = InferTensorContents.newBuilder().addAllIntContents(ints0);
        var input0 = ModelInferRequest.InferInputTensor.newBuilder()
                .setName("INPUT0").setDatatype("INT32").addShape(1).addShape(16)
                .setContents(content0);
        var ints1 = IntStream.range(0, 16).boxed().collect(Collectors.toList());
        var content1 = InferTensorContents.newBuilder().addAllIntContents(ints1);
        var input1 = ModelInferRequest.InferInputTensor.newBuilder()
                .setName("INPUT1").setDatatype("INT32").addShape(1).addShape(16)
                .setContents(content1);
        return ModelInferRequest.newBuilder()
                .addInputs(0, input0).addInputs(1, input1)
                .build();
    }

    void postprocess(Exchange exchange) {
        var response = exchange.getMessage().getBody(ModelInferResponse.class);
        var outList = response.getRawOutputContentsList().stream()
                .map(ByteString::asReadOnlyByteBuffer)
                .map(buf -> buf.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer())
                .map(buf -> {
                    var ints = new ArrayList<Integer>(buf.remaining());
                    while (buf.hasRemaining()) {
                        ints.add(buf.get());
                    }
                    return ints;
                })
                .collect(Collectors.toList());
        exchange.getMessage().setBody(outList);
    }
}

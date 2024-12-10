// camel-k: dependency=camel:djl

//DEPS org.apache.camel:camel-bom:4.9.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-djl
//DEPS ai.djl.pytorch:pytorch-engine:0.29.0
//DEPS ai.djl.pytorch:pytorch-model-zoo:0.29.0

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.apache.camel.builder.RouteBuilder;

import ai.djl.MalformedModelException;
import ai.djl.Model;
import ai.djl.modality.audio.translator.SpeechRecognitionTranslator;
import ai.djl.util.ZipUtils;

public class speech_to_text extends RouteBuilder {

    static final String MODEL_URL = "https://resources.djl.ai/test-models/pytorch/wav2vec2.zip";
    static final String MODEL_NAME = "wav2vec2.ptl";

    @Override
    public void configure() throws Exception {
        loadSpeechToTextModel();

        // @formatter:off
        from("file:data/inbox?include=.*\\.wav")
            .log("Processing: ${headers.camelFileName}")
            .to("djl:audio?model=SpeechToTextModel&translator=SpeechToTextTranslator")
            // The output of the model is all uppercase, which tends to be recognised
            // as negative by the following distilbert model
            .setBody(simple("${body.toLowerCase()}"))
            .log("  => ${body}")
            .to("djl:nlp/sentiment_analysis?artifactId=distilbert")
            .log("  => ${body.best}");
        // @formatter:on
    }

    void loadSpeechToTextModel() throws IOException, MalformedModelException, URISyntaxException {
        // Load a model
        var model = Model.newInstance(MODEL_NAME);
        // TfModel doesn't allow direct loading from remote input stream yet
        // https://github.com/deepjavalibrary/djl/issues/3303
        var modelDir = Files.createTempDirectory(MODEL_NAME);
        ZipUtils.unzip(new URI(MODEL_URL).toURL().openStream(), modelDir);
        model.load(modelDir);

        // Bind model beans
        var context = getContext();
        context.getRegistry().bind("SpeechToTextModel", model);
        context.getRegistry().bind("SpeechToTextTranslator", new SpeechRecognitionTranslator());
    }
}

// camel-k: dependency=camel:djl

//DEPS org.apache.camel:camel-bom:4.10.2@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-djl
//DEPS ai.djl.pytorch:pytorch-engine:0.29.0
//DEPS ai.djl.pytorch:pytorch-model-zoo:0.29.0
//DEPS net.sf.extjwnl:extjwnl:2.0.5
//DEPS net.sf.extjwnl:extjwnl-data-wn31:1.2

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.djl.DJLConstants;

import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.dictionary.Dictionary;

public class image_to_text extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("file:data/inbox?include=.*\\.(jpg|png)")
            .log("Processing: ${headers.camelFileName}")
            .to("djl:cv/object_detection?artifactId=ssd")
            .convertBodyTo(Image[].class)
            .split(body())
                .to("djl:cv/image_classification?artifactId=resnet")
                .wireTap("direct:save")
                //.log("  => ${body.best.className}")
                /*
                 * The output from the image classification model is classified
                 * as one of 1000 labels from WordNet.
                 * Since it's too fine-grained, we want to find the higher-level
                 * group (= hypernym) for the classification using the WordNet
                 * dictionary.
                 */
                .process(this::extractClassName)
                //.log("  => ${body}")
                .process(this::addHypernym)
                .log("  => ${body}");

        from("direct:save")
            .setBody(header(DJLConstants.INPUT))
            .setHeader(DJLConstants.FILE_TYPE, simple("${file:name.ext}"))
            .convertBodyTo(byte[].class)
            .to("file:data/output?fileName=${file:onlyname.noext}-${date:now:HHmmssSSS}.${file:ext}");
        // @formatter:on
    }

    void extractClassName(Exchange exchange) {
        var body = exchange.getMessage().getBody(Classifications.class);
        var className = body.best().getClassName().split(",")[0].split(" ", 2)[1];
        exchange.getMessage().setBody(className);
    }

    void addHypernym(Exchange exchange) throws Exception {
        var className = exchange.getMessage().getBody(String.class);
        var dic = Dictionary.getDefaultResourceInstance();
        var word = dic.getIndexWord(POS.NOUN, className);
        if (word == null) {
            throw new RuntimeCamelException("Word not found: " + className);
        }
        var hypernyms = PointerUtils.getDirectHypernyms(word.getSenses().get(0));
        var hypernym = hypernyms.stream()
                .map(h -> h.getSynset().getWords().get(0).getLemma())
                .findFirst().orElse(className);
        exchange.getMessage().setBody(List.of(className, hypernym));
    }
}

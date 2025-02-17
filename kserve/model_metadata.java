//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-kserve

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kserve.KServeComponent;

public class model_metadata extends RouteBuilder {

    static String TARGET = "localhost:8001";

    @Override
    public void configure() throws Exception {
        // Set up the target KServe server to use
        var kserve = getCamelContext().getComponent("kserve", KServeComponent.class);
        kserve.getConfiguration().setTarget(TARGET);

        // @formatter:off
        from("timer:model-metadata?repeatCount=1")
            .to("kserve:model/metadata?modelName=simple&modelVersion=1")
            .log("Metadata:\n${body}");
        // @formatter:on
    }
}

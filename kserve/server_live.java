// camel-k: language=java dependency=camel:kserve

//DEPS org.apache.camel:camel-bom:4.10.0-SNAPSHOT@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-kserve

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kserve.KServeComponent;

public class server_live extends RouteBuilder {

    static String TARGET = "localhost:8001";

    @Override
    public void configure() throws Exception {
        // Set up the target KServe server to use
        var kserve = getCamelContext().getComponent("kserve", KServeComponent.class);
        kserve.getConfiguration().setTarget(TARGET);

        // @formatter:off
        from("timer:server-live?repeatCount=1")
            .to("kserve:server/live")
            .log("Live: ${body.live}");
        // @formatter:on
    }
}

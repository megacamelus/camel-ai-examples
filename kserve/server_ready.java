// camel-k: language=java dependency=camel:kserve

//DEPS org.apache.camel:camel-bom:4.10.0-SNAPSHOT@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-kserve

import org.apache.camel.builder.RouteBuilder;

public class server_ready extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:server-ready?repeatCount=1")
            .to("kserve:server/ready")
            .log("Ready: ${body.ready}");
        // @formatter:on
    }
}

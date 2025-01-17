// camel-k: language=java dependency=camel:kserve

//DEPS org.apache.camel:camel-bom:4.10.0-SNAPSHOT@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-kserve

import org.apache.camel.builder.RouteBuilder;

public class server_metadata extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:server-metadata?repeatCount=1")
            .to("kserve:server/metadata")
            .log("Metadata:\n${body}");
        // @formatter:on
    }
}

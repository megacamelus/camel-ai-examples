//DEPS org.apache.camel:camel-bom:4.10.0@pom
//DEPS org.apache.camel:camel-core
//DEPS org.apache.camel:camel-kserve

import org.apache.camel.builder.RouteBuilder;

public class server_live extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("timer:server-live?repeatCount=1")
            .to("kserve:server/live")
            .log("Live: ${body.live}");
        // @formatter:on
    }
}

package pl.konradboniecki.chassis.configuration;


import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class WebServerConfiguration {
    @Bean
    @Primary
    public ServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory undertow = new UndertowServletWebServerFactory();
//        undertow.addBuilderCustomizers(builder -> builder.addHttpListener(8080, "0.0.0.0"));
        //builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true);
        //TODO: server.http2.enabled=true
        return undertow;

        //TODO: https to https redirection
//        undertow.addDeploymentInfoCustomizers(deploymentInfo -> {
//            deploymentInfo.addSecurityConstraint(
//                    new SecurityConstraint()
//                    .addWebResourceCollection(new WebResourceCollection().addUrlPattern("/*"))
//                    .setTransportGuaranteeType(TransportGuaranteeType.CONFIDENTIAL)
//                    .setEmptyRoleSemantic(SecurityInfo.EmptyRoleSemantic.PERMIT))
//                    .setConfidentialPortManager(exchange -> 443);
//        });

        //todo server.ssl.protocol=TLSv1.2
        //server.ssl.key-store-type=PKCS12
        //server.ssl.key-store=keystore.p12
        //server.ssl.key-store-password=xxxxxxx
        //server.port=443
        //server.use-forward-headers=true
    }
}
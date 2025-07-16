package mj.ecom.orderservice.clients;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Autowired(required = false)
    private ObservationRegistry observationRegistry;
    @Autowired(required = false)
    private Tracer tracer;
    @Autowired(required = false)
    private Propagator propagator;

    @Bean
    @LoadBalanced
    public RestClient.Builder getRestClientBuilder() {
        RestClient.Builder builder = RestClient.builder();
        if (observationRegistry != null) {
            builder.requestInterceptor(createTracingInterceptor());
        }
        return builder;
    }

    private ClientHttpRequestInterceptor createTracingInterceptor() {
        return ((req, body, execution) -> {
            if (tracer != null && propagator !=null && tracer.currentSpan() != null) {
                propagator.inject(tracer.currentTraceContext().context(), req.getHeaders(), (carrier, key, value) -> carrier.add(key, value));
            }
            return execution.execute(req, body);
        });
    }
}

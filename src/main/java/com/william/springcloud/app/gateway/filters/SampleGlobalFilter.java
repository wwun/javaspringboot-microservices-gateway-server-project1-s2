package com.william.springcloud.app.gateway.filters;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered{

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @SuppressWarnings("null")
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("ejecutando el filtro antes del request PRE");
        
        exchange.getRequest().mutate().headers(h -> h.add("token", "abcdef"));

        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            logger.info("ejecutando filtro POST responde");
            String token = exchange.getRequest().getHeaders().get("token").get(0);  //es igual a usar getFirst
            if(token!=null){
                logger.info("token: "+token);
            }

            Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(value -> {
                logger.info("token" + value);
                exchange.getResponse().getHeaders().add("token", value);
            });

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            //exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN); //para ya no mostrar la respuesta como texto planose comenta
        })); 
    }

    @Override
    public int getOrder() {
        return 100;
    }

}

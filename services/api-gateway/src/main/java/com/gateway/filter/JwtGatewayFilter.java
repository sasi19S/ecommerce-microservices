package com.gateway.filter;

import com.gateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;

@Component
public class JwtGatewayFilter implements GatewayFilter {

    @Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

    String path = exchange.getRequest().getURI().getPath();

    // Allow login endpoint without token
    if (path.contains("/auth/login")) {
        return chain.filter(exchange);
    }

    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        throw new RuntimeException("Missing or Invalid Authorization Header");
    }

    return chain.filter(exchange);
}
}
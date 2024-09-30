package co.com.btgpactual.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/transactions"), handler::getClientTransactions)
                .andRoute(GET("/funds"), request -> handler.getFunds())
                .andRoute(POST("/subscribe"), handler::subscribeToFund)
                .andRoute(PUT("/unsubscribe"), handler::unsubscribeFromFund);
    }
}

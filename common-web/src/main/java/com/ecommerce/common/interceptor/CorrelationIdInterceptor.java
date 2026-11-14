
package com.ecommerce.common.interceptor;

import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class CorrelationIdInterceptor
        implements ClientHttpRequestInterceptor {

    private static final String HEADER_NAME = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution) throws IOException {

        String correlationId = MDC.get(MDC_KEY);

        if (correlationId != null) {
            request.getHeaders().set(HEADER_NAME, correlationId);
        }

        return execution.execute(request, body);
    }
}

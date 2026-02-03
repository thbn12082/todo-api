package com.binh.todo_api.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Request-Id";
    public static final String MDC_KEY = "requestId";
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long start = System.nanoTime();
        String requestId = request.getHeader(HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        // Put into MDC so every log line can include rid
        MDC.put(MDC_KEY, requestId);

        // Return the request id to client as well
        response.setHeader(HEADER, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long tookMs = (System.nanoTime() - start) / 1_000_000;


            // 1 dòng log kết thúc request (đủ để trace & debug latency)
            // (Log thực tế nên dùng logger, nhưng để minimal, bạn có thể tự thay bằng logger)
            org.slf4j.LoggerFactory.getLogger("HTTP")
                    .info("{} {} -> {} ({}ms)",
                            request.getMethod(),
                            request.getRequestURI(),
                            response.getStatus(),
                            tookMs);

            // MUST clear to avoid leaking to next request
            MDC.remove(MDC_KEY);
        }
    }
}

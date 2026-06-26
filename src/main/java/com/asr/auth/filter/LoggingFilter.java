package com.asr.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip multipart or large file uploads if necessary
        if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String correlationId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logRequest(requestWrapper, correlationId);
            logResponse(responseWrapper, correlationId, duration);
            responseWrapper.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, String correlationId) {
        String payload = getPayload(request.getContentAsByteArray(), request.getCharacterEncoding());
        log.info("[{}] REQUEST: {} {} | Client IP: {} | Payload: {}",
                correlationId, request.getMethod(), request.getRequestURI(), request.getRemoteAddr(), payload);
    }

    private void logResponse(ContentCachingResponseWrapper response, String correlationId, long duration) {
        String payload = getPayload(response.getContentAsByteArray(), response.getCharacterEncoding());
        log.info("[{}] RESPONSE: Status {} | Duration: {}ms | Payload: {}",
                correlationId, response.getStatus(), duration, payload);
    }

    private String getPayload(byte[] buf, String characterEncoding) {
        if (buf.length > 0) {
            int length = Math.min(buf.length, 5120); // Limit payload logging to 5KB
            try {
                return new String(buf, 0, length, characterEncoding).replaceAll("[\\r\\n]+", " ");
            } catch (UnsupportedEncodingException ex) {
                return "[Unsupported Encoding]";
            }
        }
        return "[Empty]";
    }
}

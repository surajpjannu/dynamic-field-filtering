package com.jannu.springboot.dynamicfieldfiltering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bohnman.squiggly.Squiggly;
import com.github.bohnman.squiggly.util.SquigglyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.IOException;

@ControllerAdvice
public class DynamicFieldFilteringConfiguration implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(DynamicFieldFilteringConfiguration.class);

    private static final String REQUIRED_FIELDS_PARAM_KEY = "fields";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // Inorder to invoke beforeBodyWrite we should return true
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        final String requiredFieldString =
                ((ServletServerHttpRequest) request).getServletRequest().getParameter(REQUIRED_FIELDS_PARAM_KEY);
        if (null != requiredFieldString) {
            final ObjectMapper objectMapper = Squiggly.init(new ObjectMapper(), requiredFieldString);
            final String stringifyResponse = SquigglyUtils.stringify(objectMapper, body);
            try {
                return objectMapper.readValue(stringifyResponse,Object.class);
            } catch (IOException e) {
                logger.error("Failed to Parse the response : " + e.getMessage(),e);
            }
        }
        return body;
    }
}

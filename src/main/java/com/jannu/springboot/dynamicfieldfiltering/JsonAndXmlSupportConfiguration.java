package com.jannu.springboot.dynamicfieldfiltering;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class JsonAndXmlSupportConfiguration implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorPathExtension(true)
                .favorParameter(true)
                .parameterName("mediaType")
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for(HttpMessageConverter converter: converters) {
            if(converter instanceof MappingJackson2HttpMessageConverter) {
                final ObjectMapper mapper = ((MappingJackson2HttpMessageConverter)converter).getObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            }else if(converter instanceof MappingJackson2XmlHttpMessageConverter){
                final ObjectMapper mapper = ((MappingJackson2XmlHttpMessageConverter)converter).getObjectMapper();
                mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            }
        }
    }
}

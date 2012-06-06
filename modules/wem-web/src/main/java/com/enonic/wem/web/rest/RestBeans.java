package com.enonic.wem.web.rest;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.enonic.wem.web.rest.converter.JsonHttpMessageConverter;
import com.enonic.wem.web.rest.exception.HttpStatusExceptionResolver;

@EnableWebMvc
@Configuration
public class RestBeans
    extends WebMvcConfigurerAdapter
{
    @Override
    public void configureMessageConverters( final List<HttpMessageConverter<?>> converters )
    {
        converters.add( new ByteArrayHttpMessageConverter() );
        converters.add( new JsonHttpMessageConverter() );
        converters.add( new BufferedImageHttpMessageConverter() );
    }

    @Override
    public void configureHandlerExceptionResolvers( final List<HandlerExceptionResolver> exceptionResolvers )
    {
        exceptionResolvers.add( new HttpStatusExceptionResolver() );
    }
}

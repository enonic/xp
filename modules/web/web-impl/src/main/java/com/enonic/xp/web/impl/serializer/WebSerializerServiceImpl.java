package com.enonic.xp.web.impl.serializer;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.serializer.WebSerializerService;

@Component(configurationPid = "com.enonic.xp.web.jetty")
public final class WebSerializerServiceImpl
    implements WebSerializerService
{
    private volatile long maxRequestBodySize = WebSerializerConfig.DEFAULT_MAX_REQUEST_BODY_SIZE;

    @Activate
    @Modified
    public void activate( final WebSerializerConfig config )
    {
        this.maxRequestBodySize = config.http_maxRequestBodySize();
    }

    @Override
    public WebRequest request( final HttpServletRequest httpRequest )
    {
        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( httpRequest );
        return webRequest;
    }

    @Override
    public Object readBody( final HttpServletRequest httpRequest )
        throws IOException
    {
        return RequestBodyReader.readBody( httpRequest, this.maxRequestBodySize );
    }

    @Override
    public void response( final WebRequest webRequest, final WebResponse webResponse, final HttpServletResponse response )
        throws IOException
    {
        new ResponseSerializer( webRequest, webResponse ).serialize( response );
    }
}

package com.enonic.xp.web.impl.serializer;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.serializer.WebSerializerService;

@Component
public final class WebSerializerServiceImpl
    implements WebSerializerService
{
    @Override
    public WebRequest request( final HttpServletRequest httpRequest )
    {
        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( httpRequest );
        return webRequest;
    }

    @Override
    public void response( final WebRequest webRequest, final WebResponse webResponse, final HttpServletResponse response )
        throws IOException
    {
        new ResponseSerializer( webRequest, webResponse ).serialize( response );
    }
}

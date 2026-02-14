package com.enonic.xp.web.impl.serializer;

import org.osgi.service.component.annotations.Component;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.serializer.RequestSerializerService;

@Component
public class RequestSerializerServiceImpl
    implements RequestSerializerService
{
    @Override
    public WebRequest serialize( final HttpServletRequest httpRequest )
    {
        final WebRequest webRequest = new WebRequest();
        new RequestSerializer( webRequest ).serialize( httpRequest );
        return webRequest;
    }
}

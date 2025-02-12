package com.enonic.xp.web.impl.serializer;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.serializer.ResponseSerializationService;

@Component
public final class ResponseSerializationServiceImpl
    implements ResponseSerializationService
{
    @Override
    public void serialize( final WebRequest webRequest, final WebResponse webResponse, final HttpServletResponse response )
        throws IOException
    {
        new ResponseSerializer( webRequest, webResponse ).serialize( response );
    }
}

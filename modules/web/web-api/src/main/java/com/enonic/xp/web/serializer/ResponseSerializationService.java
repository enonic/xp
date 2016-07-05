package com.enonic.xp.web.serializer;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface ResponseSerializationService
{
    void serialize( final WebRequest webRequest, final WebResponse webResponse, final HttpServletResponse response )
        throws IOException;
}

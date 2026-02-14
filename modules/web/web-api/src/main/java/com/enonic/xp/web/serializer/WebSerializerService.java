package com.enonic.xp.web.serializer;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface WebSerializerService
{
    WebRequest request( HttpServletRequest httpRequest );

    void response( WebRequest webRequest, WebResponse webResponse, HttpServletResponse httpResponse )
        throws IOException;
}

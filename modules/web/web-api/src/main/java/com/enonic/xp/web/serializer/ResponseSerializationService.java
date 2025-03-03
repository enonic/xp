package com.enonic.xp.web.serializer;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface ResponseSerializationService
{
    void serialize( WebRequest webRequest, WebResponse webResponse, HttpServletResponse response )
        throws IOException;
}

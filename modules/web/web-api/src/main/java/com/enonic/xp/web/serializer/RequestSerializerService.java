package com.enonic.xp.web.serializer;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.web.WebRequest;

public interface RequestSerializerService
{
    WebRequest serialize( HttpServletRequest httpRequest );
}

package com.enonic.xp.web.api;

import com.enonic.xp.api.ApiDescriptor;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

public interface ApiHandler
{
    String APPLICATION_KEY_PROPERTY = "applicationKey";

    String API_KEY_PROPERTY = "apiKey";

    String ALLOWED_PRINCIPALS_PROPERTY = "allowedPrincipals";

    String DEFAULT_API_KEY = "api";

    ApiDescriptor getDescriptor();

    WebResponse handle( WebRequest request )
        throws Exception;
}

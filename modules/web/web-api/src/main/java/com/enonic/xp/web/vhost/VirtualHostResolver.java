package com.enonic.xp.web.vhost;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualHostResolver
{
    VirtualHost resolveVirtualHost( HttpServletRequest req );
}

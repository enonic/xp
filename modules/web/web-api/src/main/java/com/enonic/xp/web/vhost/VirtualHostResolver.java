package com.enonic.xp.web.vhost;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface VirtualHostResolver
{
    VirtualHost resolveVirtualHost( HttpServletRequest req );
}

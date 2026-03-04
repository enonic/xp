package com.enonic.xp.web.vhost;

import jakarta.servlet.http.HttpServletRequest;


public interface VirtualHostResolver
{
    VirtualHost resolveVirtualHost( HttpServletRequest req );
}

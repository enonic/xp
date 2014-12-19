package com.enonic.xp.web.vhost.impl;

import javax.servlet.http.HttpServletRequest;

public interface VirtualHostResolver
{
    public boolean requireVirtualHost();

    public VirtualHost resolve( HttpServletRequest req );
}

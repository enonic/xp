package com.enonic.xp.web.vhost.impl.resolver;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.impl.mapping.VirtualHostMapping;

final class VirtualHostResolver
{
    private final List<VirtualHostMapping> mappings;

    public VirtualHostResolver( final List<VirtualHostMapping> mappings )
    {
        this.mappings = mappings;
    }

    public VirtualHost resolve( final HttpServletRequest req )
    {
        return null;
    }
}

package com.enonic.xp.portal.impl.filter;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.site.filter.FilterDescriptors;

public interface FilterChainResolver
{
    FilterDescriptors resolve( PortalRequest request );
}

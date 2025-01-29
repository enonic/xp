package com.enonic.xp.portal.impl.url3;

import com.enonic.xp.portal.PortalRequest;

public class HarmonizedApiPathPrefixStrategy
    implements PathPrefixStrategy
{
    private final PortalRequest portalRequest;

    public HarmonizedApiPathPrefixStrategy( final PortalRequest portalRequest )
    {
        this.portalRequest = portalRequest;
    }

    @Override
    public String generatePathPrefix()
    {
        return "/site/project/branch/mysite/_";
    }
}

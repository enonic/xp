package com.enonic.xp.portal.rest;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public final class PortalRestServiceNotFoundException
    extends NotFoundException
{
    public PortalRestServiceNotFoundException( final String portalRestServiceName )
    {
        super( "Portal Rest service [{0}] was not found", portalRestServiceName );
    }
}

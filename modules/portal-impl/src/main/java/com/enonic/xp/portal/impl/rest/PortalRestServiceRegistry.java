package com.enonic.xp.portal.impl.rest;

import com.enonic.xp.portal.rest.PortalRestService;

public interface PortalRestServiceRegistry
{
    PortalRestService getPortalRestService( String portalRestServiceName );
}

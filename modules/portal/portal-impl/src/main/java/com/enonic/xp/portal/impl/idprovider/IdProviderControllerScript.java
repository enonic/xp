package com.enonic.xp.portal.impl.idprovider;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface IdProviderControllerScript
{
    boolean hasMethod( String functionName );

    PortalResponse execute( String functionName, PortalRequest portalRequest );
}

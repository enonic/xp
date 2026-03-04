package com.enonic.xp.portal.postprocess;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;


public interface PostProcessor
{
    PortalResponse processResponse( PortalRequest portalRequest, PortalResponse portalResponse );

    PortalResponse processResponseInstructions( PortalRequest portalRequest, PortalResponse portalResponse );

    PortalResponse processResponseContributions( PortalRequest portalRequest, PortalResponse portalResponse );
}

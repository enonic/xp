package com.enonic.xp.portal.controller;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface ControllerScript
{
    PortalResponse execute( PortalRequest portalRequest );
}

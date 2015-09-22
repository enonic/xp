package com.enonic.xp.portal.impl.controller;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface ControllerScript
{
    public PortalResponse execute( PortalRequest portalRequest );
}

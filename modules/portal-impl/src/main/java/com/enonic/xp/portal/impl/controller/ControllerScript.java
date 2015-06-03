package com.enonic.xp.portal.impl.controller;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface ControllerScript
{
    public void execute( PortalRequest portalRequest, PortalResponse portalResponse );
}

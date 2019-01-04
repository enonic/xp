package com.enonic.xp.portal.idprovider;

import java.io.IOException;

import com.enonic.xp.portal.PortalResponse;

public interface IdProviderControllerService
{
    PortalResponse execute( IdProviderControllerExecutionParams params )
        throws IOException;
}

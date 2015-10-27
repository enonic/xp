package com.enonic.xp.portal.impl.exception;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

public interface ExceptionRenderer
{
    PortalResponse render( PortalRequest req, PortalException cause );
}

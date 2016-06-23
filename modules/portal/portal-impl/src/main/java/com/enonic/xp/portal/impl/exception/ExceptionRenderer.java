package com.enonic.xp.portal.impl.exception;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.WebException;

public interface ExceptionRenderer
{
    PortalResponse render( PortalRequest req, WebException cause );
}

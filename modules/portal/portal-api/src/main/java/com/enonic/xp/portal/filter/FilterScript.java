package com.enonic.xp.portal.filter;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandlerChain;

public interface FilterScript
{
    PortalResponse execute( PortalRequest request, WebResponse response, WebHandlerChain webHandlerChain );
}

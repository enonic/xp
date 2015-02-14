package com.enonic.xp.portal.postprocess;

import com.enonic.xp.portal.PortalContext;

public interface PostProcessor
{
    void processResponse( PortalContext context );
}

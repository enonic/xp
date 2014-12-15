package com.enonic.wem.portal.internal.postprocess;


import com.enonic.xp.portal.PortalContext;

public interface PostProcessor
{
    void processResponse( PortalContext context );
}

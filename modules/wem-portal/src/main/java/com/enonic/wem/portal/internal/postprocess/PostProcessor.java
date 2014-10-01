package com.enonic.wem.portal.internal.postprocess;


import com.enonic.wem.portal.PortalContext;

public interface PostProcessor
{
    void processResponse( PortalContext context );
}

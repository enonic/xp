package com.enonic.xp.portal.postprocess;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalContext;

@Beta
public interface PostProcessor
{
    void processResponse( PortalContext context );
}

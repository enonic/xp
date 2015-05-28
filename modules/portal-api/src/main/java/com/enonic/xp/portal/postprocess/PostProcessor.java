package com.enonic.xp.portal.postprocess;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalRequest;

@Beta
public interface PostProcessor
{
    void processResponse( PortalRequest portalRequest );
}

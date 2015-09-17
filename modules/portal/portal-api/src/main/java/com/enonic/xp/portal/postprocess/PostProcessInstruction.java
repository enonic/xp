package com.enonic.xp.portal.postprocess;

import com.google.common.annotations.Beta;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

@Beta
public interface PostProcessInstruction
{
    PortalResponse evaluate( PortalRequest portalRequest, String instruction );
}

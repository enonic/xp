package com.enonic.xp.portal.postprocess;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

@PublicApi
public interface PostProcessInstruction
{
    PortalResponse evaluate( PortalRequest portalRequest, String instruction );
}

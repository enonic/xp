package com.enonic.xp.portal.postprocess;

import com.enonic.xp.portal.PortalContext;

public interface PostProcessInstruction
{
    public String evaluate( PortalContext context, String instruction );
}

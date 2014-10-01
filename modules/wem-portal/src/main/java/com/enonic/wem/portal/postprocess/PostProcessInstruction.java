package com.enonic.wem.portal.postprocess;

import com.enonic.wem.portal.PortalContext;

public interface PostProcessInstruction
{
    public String evaluate( PortalContext context, String instruction );
}

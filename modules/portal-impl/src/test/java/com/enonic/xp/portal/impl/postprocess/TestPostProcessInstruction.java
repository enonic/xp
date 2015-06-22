package com.enonic.xp.portal.impl.postprocess;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

public final class TestPostProcessInstruction
    implements PostProcessInstruction
{
    @Override
    public String evaluate( final PortalRequest portalRequest, final String instruction )
    {
        return instruction.startsWith( "TEST " ) ? "Testing" : null;
    }
}

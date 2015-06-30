package com.enonic.xp.portal.impl.postprocess;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.postprocess.PostProcessInstruction;

public final class TestPostProcessInstruction
    implements PostProcessInstruction
{
    @Override
    public PortalResponse evaluate( final PortalRequest portalRequest, final String instruction )
    {
        return instruction.startsWith( "TEST " ) ? PortalResponse.create().body( "Testing" ).build() : null;
    }
}

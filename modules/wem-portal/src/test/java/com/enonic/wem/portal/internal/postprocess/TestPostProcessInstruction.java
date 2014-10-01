package com.enonic.wem.portal.internal.postprocess;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.postprocess.PostProcessInstruction;

public final class TestPostProcessInstruction
    implements PostProcessInstruction
{
    @Override
    public String evaluate( final PortalContext context, final String instruction )
    {
        return instruction;
    }
}

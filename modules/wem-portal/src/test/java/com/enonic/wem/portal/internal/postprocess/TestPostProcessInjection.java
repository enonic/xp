package com.enonic.wem.portal.internal.postprocess;

import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.postprocess.PostProcessInjection;

public final class TestPostProcessInjection
    implements PostProcessInjection
{
    @Override
    public String inject( final PortalContext context, final Tag tag )
    {
        return "<!-- " + tag.toString() + "-->";
    }
}

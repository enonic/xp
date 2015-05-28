package com.enonic.xp.portal.impl.postprocess;

import java.util.Arrays;
import java.util.List;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.portal.postprocess.PostProcessInjection;

public final class TestPostProcessInjection
    implements PostProcessInjection
{
    @Override
    public List<String> inject( final PortalContext context, final HtmlTag htmlTag )
    {
        return Arrays.asList( "<!-- " + htmlTag.toString() + "-->" );
    }
}

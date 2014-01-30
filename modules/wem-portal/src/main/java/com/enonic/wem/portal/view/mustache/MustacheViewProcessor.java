package com.enonic.wem.portal.view.mustache;

import com.enonic.wem.portal.view.RenderViewSpec;
import com.enonic.wem.portal.view.ViewProcessor;

public final class MustacheViewProcessor
    implements ViewProcessor
{
    @Override
    public String getName()
    {
        return "mustache";
    }

    @Override
    public String process( final RenderViewSpec spec )
    {
        return null;
    }
}

package com.enonic.wem.portal.postprocess;

import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.postprocess.injection.PostProcessInjection;

public final class TestPostProcessInjection
    implements PostProcessInjection
{
    @Override
    public String inject( final JsContext context, final Tag tag )
    {
        return "<!-- " + tag.toString() + "-->";
    }
}

package com.enonic.wem.portal.internal.postprocess;

import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.postprocess.injection.PostProcessInjection;

public final class TestPostProcessInjection
    implements PostProcessInjection
{
    @Override
    public String inject( final JsContext context, final Tag tag )
    {
        return "<!-- " + tag.toString() + "-->";
    }
}

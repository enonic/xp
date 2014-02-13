package com.enonic.wem.portal.postprocess.inject;

import javax.inject.Singleton;

import com.enonic.wem.portal.controller.JsContext;

@Singleton
public final class LiveEditInjection
    implements PostProcessInjection
{
    @Override
    public String inject( final JsContext context, final Location location )
    {
        return null;
    }
}

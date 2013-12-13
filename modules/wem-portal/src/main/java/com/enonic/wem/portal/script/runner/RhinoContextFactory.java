package com.enonic.wem.portal.script.runner;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

final class RhinoContextFactory
    extends ContextFactory
{
    @Override
    protected Context makeContext()
    {
        final Context context = super.makeContext();
        context.setOptimizationLevel( 9 );
        return context;
    }
}

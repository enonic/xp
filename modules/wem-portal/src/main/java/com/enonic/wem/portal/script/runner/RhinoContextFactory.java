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
        context.setLanguageVersion( Context.VERSION_1_8 );
        context.setOptimizationLevel( 9 );
        return context;
    }
}

package com.enonic.wem.core.script.service;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

final class RhinoContextFactory
    extends ContextFactory
{
    @Override
    protected void onContextCreated( final Context cx )
    {
        super.onContextCreated( cx );
        cx.setLanguageVersion( Context.VERSION_1_8 );
        cx.setOptimizationLevel( 9 );
    }
}

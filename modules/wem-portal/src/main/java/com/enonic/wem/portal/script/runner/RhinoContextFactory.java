package com.enonic.wem.portal.script.runner;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

import com.enonic.wem.portal.script.wrapper.RhinoWrapFactory;

final class RhinoContextFactory
    extends ContextFactory
{
    private final RhinoWrapFactory wrapFactory;

    public RhinoContextFactory()
    {
        this.wrapFactory = new RhinoWrapFactory();
    }

    @Override
    protected void onContextCreated( final Context cx )
    {
        super.onContextCreated( cx );
        cx.setLanguageVersion( Context.VERSION_1_8 );
        cx.setOptimizationLevel( 9 );
        cx.setWrapFactory( this.wrapFactory );
    }
}

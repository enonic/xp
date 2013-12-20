package com.enonic.wem.portal.script.lib;

import javax.inject.Inject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;

public final class GlobalScriptBean
    extends ImporterTopLevel
{
    @Inject
    protected SystemScriptBean system;

    public void initialize()
    {
        register( SystemScriptBean.NAME, this.system );
    }

    private void register( final String name, final Object value )
    {
        put( name, this, Context.toObject( value, this ) );
    }

    public void setSystem( final SystemScriptBean system )
    {
        this.system = system;
    }
}

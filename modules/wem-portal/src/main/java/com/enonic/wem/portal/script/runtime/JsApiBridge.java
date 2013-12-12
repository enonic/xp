package com.enonic.wem.portal.script.runtime;

import javax.inject.Inject;

import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.Commands;

public final class JsApiBridge
{
    @Inject
    protected Client client;

    protected Scriptable scope;

    public Client getClient()
    {
        return this.client;
    }

    public NativeJavaClass getCommands()
    {
        return new NativeJavaClass( this.scope, Commands.class );
    }

    public void setScope( final Scriptable scope )
    {
        this.scope = scope;
    }
}

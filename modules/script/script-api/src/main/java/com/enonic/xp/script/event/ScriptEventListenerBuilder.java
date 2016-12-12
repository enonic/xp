package com.enonic.xp.script.event;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.enonic.xp.app.ApplicationKey;

public final class ScriptEventListenerBuilder
{
    private ScriptEventListenerImpl listener;

    public ScriptEventListenerBuilder()
    {
        this.listener = new ScriptEventListenerImpl();
    }

    public ScriptEventListenerBuilder application( final ApplicationKey application )
    {
        this.listener.application = application;
        return this;
    }

    public ScriptEventListenerBuilder typePattern( final String pattern )
    {
        this.listener.typePattern = Pattern.compile( pattern.replace( ".", "\\." ).replace( "*", ".*" ) );
        return this;
    }

    public ScriptEventListenerBuilder listener( final Consumer<Object> listener )
    {
        this.listener.listener = listener;
        return this;
    }

    public ScriptEventListenerBuilder localOnly( final boolean localOnly )
    {
        this.listener.localOnly = localOnly;
        return this;
    }

    public ScriptEventListener build()
    {
        return this.listener;
    }
}

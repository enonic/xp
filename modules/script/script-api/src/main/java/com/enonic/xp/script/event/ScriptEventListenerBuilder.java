package com.enonic.xp.script.event;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.enonic.xp.app.ApplicationKey;

public final class ScriptEventListenerBuilder
{
    private ApplicationKey application;

    private Pattern typePattern;

    private Consumer<Object> listener;

    private boolean localOnly;

    public ScriptEventListenerBuilder application( final ApplicationKey application )
    {
        this.application = application;
        return this;
    }

    public ScriptEventListenerBuilder typePattern( final String pattern )
    {
        this.typePattern = Pattern.compile( pattern.replace( ".", "\\." ).replace( "*", ".*" ) );
        return this;
    }

    public ScriptEventListenerBuilder listener( final Consumer<Object> listener )
    {
        this.listener = listener;
        return this;
    }

    public ScriptEventListenerBuilder localOnly( final boolean localOnly )
    {
        this.localOnly = localOnly;
        return this;
    }

    public ScriptEventListener build()
    {
        return new ScriptEventListenerImpl( application, typePattern, listener, localOnly );
    }
}

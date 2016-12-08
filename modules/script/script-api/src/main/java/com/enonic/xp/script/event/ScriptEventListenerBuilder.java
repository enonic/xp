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

    public ScriptEventListenerBuilder pattern( final String pattern )
    {
        this.listener.pattern = Pattern.compile( pattern.replace( ".", "\\." ).replace( "*", ".*" ) );
        return this;
    }

    public ScriptEventListenerBuilder listener( final Consumer<Object> listener )
    {
        this.listener.listener = listener;
        return this;
    }

    public ScriptEventListener build()
    {
        return this.listener;
    }
}

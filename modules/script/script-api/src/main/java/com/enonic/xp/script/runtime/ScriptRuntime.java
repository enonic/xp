package com.enonic.xp.script.runtime;

import java.util.concurrent.CompletableFuture;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;

public interface ScriptRuntime
{
    boolean hasScript( ResourceKey script );

    ScriptExports execute( ResourceKey script );

    CompletableFuture<ScriptExports> executeAsync( ResourceKey script );

    ScriptValue toScriptValue( ResourceKey script, Object value );

    Object toNativeObject( ResourceKey script, Object value );

    void invalidate( ApplicationKey key );
}

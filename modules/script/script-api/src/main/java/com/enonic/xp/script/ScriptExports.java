package com.enonic.xp.script;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.resource.ResourceKey;

@PublicApi
public interface ScriptExports
{
    ResourceKey getScript();

    ScriptValue getValue();

    boolean hasMethod( String name );

    ScriptValue executeMethod( String name, Object... args );

    Object getRawValue();
}

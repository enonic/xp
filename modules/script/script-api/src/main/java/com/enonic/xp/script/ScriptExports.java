package com.enonic.xp.script;

import com.enonic.xp.resource.ResourceKey;


public interface ScriptExports
{
    ResourceKey getScript();

    ScriptValue getValue();

    boolean hasMethod( String name );

    ScriptValue executeMethod( String name, Object... args );

    Object getRawValue();
}

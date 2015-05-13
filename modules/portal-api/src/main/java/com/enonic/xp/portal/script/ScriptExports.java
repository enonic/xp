package com.enonic.xp.portal.script;

import com.google.common.annotations.Beta;

import com.enonic.xp.resource.ResourceKey;

@Beta
public interface ScriptExports
{
    ResourceKey getScript();

    ScriptValue getValue();

    boolean hasMethod( String name );

    ScriptValue executeMethod( String name, Object... args );

    Object getRawValue();
}

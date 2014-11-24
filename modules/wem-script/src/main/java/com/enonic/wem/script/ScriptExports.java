package com.enonic.wem.script;

import com.enonic.wem.api.resource.ResourceKey;

public interface ScriptExports
{
    public ResourceKey getScript();

    public boolean hasProperty( String name );

    public Object executeMethod( String name, Object... args );

    public <T> T applyToBean( T bean, Object result );
}

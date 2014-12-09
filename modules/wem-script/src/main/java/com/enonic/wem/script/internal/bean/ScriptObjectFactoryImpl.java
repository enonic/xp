package com.enonic.wem.script.internal.bean;

import com.enonic.wem.script.ScriptObject;
import com.enonic.wem.script.internal.ScriptExecutor;

public final class ScriptObjectFactoryImpl
    implements ScriptObjectFactory
{
    private final ScriptExecutor executor;

    public ScriptObjectFactoryImpl( final ScriptExecutor executor )
    {
        this.executor = executor;
    }

    @Override
    public ScriptObject create( final Object value )
    {
        return new ScriptObjectImpl( value );
    }
}

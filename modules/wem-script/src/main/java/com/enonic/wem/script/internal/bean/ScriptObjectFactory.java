package com.enonic.wem.script.internal.bean;

import com.enonic.wem.script.ScriptObject;

public interface ScriptObjectFactory
{
    public ScriptObject create( Object value );
}

package com.enonic.wem.script.internal.bean;

import com.enonic.wem.script.ScriptValue;

public interface ScriptValueFactory
{
    public ScriptMethodInvoker getInvoker();

    public ScriptValue newValue( Object value );
}

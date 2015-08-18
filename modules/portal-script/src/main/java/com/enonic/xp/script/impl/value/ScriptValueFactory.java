package com.enonic.xp.script.impl.value;

import com.enonic.xp.script.ScriptValue;

public interface ScriptValueFactory
{
    ScriptMethodInvoker getInvoker();

    ScriptValue newValue( Object value );
}

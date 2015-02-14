package com.enonic.xp.script.impl.bean;

import com.enonic.xp.portal.script.ScriptValue;

public interface ScriptValueFactory
{
    public ScriptMethodInvoker getInvoker();

    public ScriptValue newValue( Object value );
}

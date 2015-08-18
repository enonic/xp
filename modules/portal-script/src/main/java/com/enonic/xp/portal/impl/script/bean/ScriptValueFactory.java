package com.enonic.xp.portal.impl.script.bean;

import com.enonic.xp.script.ScriptValue;

public interface ScriptValueFactory
{
    public ScriptMethodInvoker getInvoker();

    public ScriptValue newValue( Object value );
}

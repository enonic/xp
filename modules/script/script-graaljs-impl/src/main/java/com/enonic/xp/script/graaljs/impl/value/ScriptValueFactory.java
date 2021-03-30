package com.enonic.xp.script.graaljs.impl.value;

import com.enonic.xp.script.ScriptValue;

public interface ScriptValueFactory
{
    ScriptValue newValue( Object value );
}

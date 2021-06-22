package com.enonic.xp.script.graaljs.impl.value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graaljs.impl.util.JavascriptHelper;

public interface ScriptValueFactory
{
    JavascriptHelper getJavascriptHelper();

    ScriptValue newValue( Object value );
}

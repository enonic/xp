package com.enonic.xp.script.impl.value;

import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.util.JavascriptHelper;

public interface ScriptValueFactory<T>
{
    JavascriptHelper<T> getJavascriptHelper();

    ScriptValue newValue( Object value );

    ScriptValue evalValue( String script );
}

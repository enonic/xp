package com.enonic.xp.script.graaljs.impl.value;

import org.graalvm.polyglot.Value;

final  class ArrayScriptValue extends AbstractScriptValue
{
    private final Value value;

    ArrayScriptValue(final Value value)
    {
        this.value = value;
    }
}

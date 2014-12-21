package com.enonic.wem.script;

import java.util.List;
import java.util.Set;

public interface ScriptValue
{
    public boolean isArray();

    public boolean isObject();

    public boolean isValue();

    public boolean isFunction();

    public boolean isUndefined();

    public Object getValue();

    public <T> T getValue( Class<T> type );

    public Set<String> getKeys();

    public boolean hasMember( String key );

    public ScriptValue getMember( String key );

    public List<ScriptValue> getArray();

    public ScriptValue call( Object... args );
}

package com.enonic.wem.script;

import java.util.List;
import java.util.Set;

public interface ScriptObject
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

    public ScriptObject getMember( String key );

    public List<ScriptObject> getArray();

    public ScriptObject call( Object... args );
}

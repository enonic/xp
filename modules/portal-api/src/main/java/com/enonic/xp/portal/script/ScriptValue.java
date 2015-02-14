package com.enonic.xp.portal.script;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ScriptValue
{
    public boolean isArray();

    public boolean isObject();

    public boolean isValue();

    public boolean isFunction();

    public Object getValue();

    public <T> T getValue( Class<T> type );

    public Set<String> getKeys();

    public boolean hasMember( String key );

    public ScriptValue getMember( String key );

    public List<ScriptValue> getArray();

    public <T> List<T> getArray( Class<T> type );

    public Map<String, Object> getMap();

    public ScriptValue call( Object... args );
}

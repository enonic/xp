package com.enonic.wem.script.internal.v2;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

final class ScriptObjectConverter
{
    private Object toObject( final Object source )
    {
        if ( source instanceof ScriptObjectMirror )
        {
            return toObject( (ScriptObjectMirror) source );
        }

        return source;
    }

    private Object toObject( final ScriptObjectMirror source )
    {
        if ( source.isArray() )
        {
            return toList( source );
        }
        else if ( source.isFunction() )
        {
            return null;
        }
        else
        {
            return toMap( source );
        }
    }

    private List<Object> toList( final ScriptObjectMirror source )
    {
        final List<Object> result = Lists.newArrayList();
        for ( final Object item : source.values() )
        {
            final Object converted = toObject( item );
            if ( converted != null )
            {
                result.add( converted );
            }
        }

        return result;
    }

    public Map<String, Object> toMap( final ScriptObjectMirror source )
    {
        final Map<String, Object> result = Maps.newHashMap();
        for ( final Map.Entry<String, Object> entry : source.entrySet() )
        {
            final Object converted = toObject( entry.getValue() );
            if ( converted != null )
            {
                result.put( entry.getKey(), converted );
            }
        }

        return result;
    }
}

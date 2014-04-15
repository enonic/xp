package com.enonic.wem.core.script.service;

import java.util.Map;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

final class MapWrapper
    implements Scriptable, Wrapper
{
    public final Map map;

    public MapWrapper( final Map map )
    {
        this.map = map;
    }

    @Override
    public boolean equals( Object o )
    {
        return ( o instanceof Map ) && this.map.equals( o );
    }

    @Override
    public int hashCode()
    {
        return this.map.hashCode();
    }

    @Override
    public void delete( final String name )
    {
        this.map.remove( name );
    }

    @Override
    public void delete( final int index )
    {
        this.map.remove( index );
    }

    @Override
    public Object get( final String name, final Scriptable start )
    {
        return this.map.get( name );
    }

    @Override
    public Object get( final int index, final Scriptable start )
    {
        return this.map.get( index );
    }

    @Override
    public String getClassName()
    {
        return this.map.getClass().getName();
    }

    @Override
    public Object getDefaultValue( final Class<?> hint )
    {
        return toString();
    }

    @Override
    public Object[] getIds()
    {
        return this.map.keySet().toArray( new Object[this.map.size()] );
    }

    @Override
    public Scriptable getParentScope()
    {
        return null;
    }

    @Override
    public Scriptable getPrototype()
    {
        return null;
    }

    @Override
    public boolean has( final String name, final Scriptable start )
    {
        return this.map.containsKey( name );
    }

    @Override
    public boolean has( final int index, final Scriptable start )
    {
        return this.map.containsKey( index );
    }

    @Override
    public boolean hasInstance( final Scriptable instance )
    {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put( final String name, final Scriptable start, final Object value )
    {
        this.map.put( name, value );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void put( final int index, final Scriptable start, final Object value )
    {
        this.map.put( index, value );
    }

    @Override
    public void setParentScope( final Scriptable parent )
    {
    }

    @Override
    public void setPrototype( final Scriptable prototype )
    {
    }

    @Override
    public Object unwrap()
    {
        return this.map;
    }
}

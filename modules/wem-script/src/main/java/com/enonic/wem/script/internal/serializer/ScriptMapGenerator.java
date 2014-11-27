package com.enonic.wem.script.internal.serializer;

import java.util.Stack;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.PropertyMap;
import jdk.nashorn.internal.runtime.ScriptObject;
import jdk.nashorn.internal.scripts.JO;

import com.enonic.wem.script.serializer.MapGenerator;

public final class ScriptMapGenerator
    implements MapGenerator
{
    private final ScriptObject root;

    private final Stack<ScriptObject> stack;

    private ScriptObject current;

    private String name;

    public ScriptMapGenerator()
    {
        this.root = newMap();
        this.stack = new Stack<>();
        this.current = this.root;
    }

    private ScriptObject newMap()
    {
        final PropertyMap map = PropertyMap.newMap();
        return new JO( map );
    }

    private ScriptObject newArray()
    {
        return Global.allocate( new Object[0] );
    }

    public Object getRootObject()
    {
        return this.root;
    }

    private String popName()
    {
        final String name = this.name;
        if ( name != null )
        {
            this.name = null;
            return name;
        }

        throw new IllegalStateException( "Name must be set" );
    }

    @Override
    public MapGenerator map()
    {
        final ScriptObject old = this.current;
        this.stack.push( old );

        this.current = newMap();

        if ( old.isArray() )
        {
            NativeArray.push( old, this.current );
        }
        else
        {
            old.put( popName(), this.current, false );
        }

        return this;
    }

    @Override
    public MapGenerator array()
    {
        final ScriptObject old = this.current;
        this.stack.push( old );

        this.current = newArray();

        if ( old.isArray() )
        {
            NativeArray.push( old, this.current );
        }
        else
        {
            old.put( popName(), this.current, false );
        }

        return this;
    }

    @Override
    public MapGenerator end()
    {
        this.current = this.stack.pop();
        return this;
    }

    @Override
    public MapGenerator name( final String name )
    {
        this.name = name;
        return this;
    }

    @Override
    public MapGenerator value( final Object value )
    {
        if ( this.current.isArray() )
        {
            NativeArray.push( this.current, value );
        }
        else
        {
            this.current.put( popName(), value, false );
        }

        return this;
    }
}

package com.enonic.wem.script.serializer;

import java.util.Stack;

public abstract class MapGeneratorBase2
    implements MapGenerator2
{
    private Object root;

    private String field;

    private final Stack<Object> stack;

    private Object current;

    public MapGeneratorBase2()
    {
        this.stack = new Stack<>();
    }

    protected abstract Object newObject();

    protected abstract Object newArray();

    protected abstract boolean isObject( Object value );

    protected abstract boolean isArray( Object value );

    protected abstract void putInObject( Object object, String key, Object value );

    protected abstract void addToArray( Object array, Object value );

    @Override
    public final Object getModel()
    {
        return this.root;
    }

    @Override
    public final MapGenerator2 field( final String name )
    {
        this.field = name;
        return this;
    }

    @Override
    public final MapGenerator2 object()
    {
        final Object object = newObject();
        addValue( object );
        setCurrent( object );
        return this;
    }

    private void addValue( final Object value )
    {
        if ( isObject( this.current ) )
        {
            putInObject( this.current, popField(), value );
            return;
        }

        if ( isArray( this.current ) )
        {
            addToArray( this.current, value );
            return;
        }

        if ( this.root == null )
        {
            this.field = null;
            this.root = value;
            this.current = this.root;
            return;
        }

        throw new IllegalArgumentException( "Not object or array" );
    }

    private void setCurrent( final Object value )
    {
        if ( this.current != value )
        {
            this.stack.push( this.current );
        }

        this.current = value;

        if ( this.root == null )
        {
            this.root = this.current;
        }
    }

    private String popField()
    {
        final String value = this.field;
        this.field = null;

        if ( value != null )
        {
            return value;
        }

        throw new IllegalArgumentException( "Field is not set" );
    }

    @Override
    public final MapGenerator2 array()
    {
        final Object array = newArray();
        addValue( array );
        setCurrent( array );
        return this;
    }

    public final MapGeneratorBase2 value( final Object value )
    {
        addValue( value );
        return this;
    }

    @Override
    public final MapGeneratorBase2 end()
    {
        if ( this.stack.isEmpty() )
        {
            return this;
        }

        this.current = this.stack.pop();
        return this;
    }
}

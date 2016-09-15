package com.enonic.xp.script.serializer;

import java.util.Stack;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteSource;

@Beta
public abstract class MapGeneratorBase
    implements MapGenerator
{
    private Object root;

    private final Stack<Object> stack;

    private Object current;

    public MapGeneratorBase()
    {
        this.stack = new Stack<>();
    }

    protected void initRoot()
    {
        this.root = newMap();
        this.current = this.root;
    }

    public final Object getRoot()
    {
        return this.root;
    }

    protected abstract Object newMap();

    protected abstract Object newArray();

    protected abstract boolean isMap( Object value );

    protected abstract boolean isArray( Object value );

    private void checkIfMap()
    {
        if ( !isMap( this.current ) )
        {
            throw new IllegalArgumentException( "Current object should be of type map" );
        }
    }

    private void checkIfArray()
    {
        if ( !isArray( this.current ) )
        {
            throw new IllegalArgumentException( "Current object should be of type array" );
        }
    }

    private Object putInMap( final String key, final Object value )
    {
        checkIfMap();
        putInMap( this.current, key, value );
        return value;
    }

    protected abstract void putInMap( Object map, String key, Object value );

    private Object addToArray( final Object value )
    {
        checkIfArray();
        addToArray( this.current, value );
        return value;
    }

    protected abstract void addToArray( Object array, Object value );

    private void setCurrent( final Object object )
    {
        this.stack.push( this.current );
        this.current = object;
    }

    @Override
    public final MapGenerator array()
    {
        setCurrent( addToArray( newArray() ) );
        return this;
    }

    @Override
    public final MapGenerator array( final String key )
    {
        setCurrent( putInMap( key, newArray() ) );
        return this;
    }

    @Override
    public final MapGenerator map()
    {
        setCurrent( addToArray( newMap() ) );
        return this;
    }

    @Override
    public final MapGenerator map( final String key )
    {
        setCurrent( putInMap( key, newMap() ) );
        return this;
    }

    @Override
    public final MapGenerator value( final Object value )
    {
        addToArray( convertValue( value ) );
        return this;
    }

    @Override
    public final MapGenerator value( final String key, final Object value )
    {
        putInMap( key, convertValue( value ) );
        return this;
    }

    @Override
    public final MapGenerator end()
    {
        if ( !this.stack.isEmpty() )
        {
            this.current = this.stack.pop();
        }

        return this;
    }

    private Object convertValue( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof Number )
        {
            return convertNumber( (Number) value );
        }

        if ( value instanceof Boolean )
        {
            return value;
        }

        if ( value instanceof ByteSource )
        {
            return value;
        }

        return value.toString();
    }

    private Object convertNumber( final Number value )
    {
        if ( value instanceof Long )
        {
            Long l = (Long) value;
            if ( l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE )
            {
                return l.intValue();
            }
        }
        return value;
    }
}

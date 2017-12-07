package com.enonic.xp.script.serializer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.common.annotations.Beta;
import com.google.common.collect.Multimap;
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
        if ( value instanceof Map )
        {
            this.map();
            serializeMap( (Map<?, ?>) value );
            return this.end();
        }

        if ( value instanceof List )
        {
            this.array();
            serializeList( (List<?>) value );
            return end();
        }

        if ( value instanceof Multimap )
        {
            this.map();
            serializeMultimap( (Multimap<?, ?>) value );
            return this.end();
        }

        return rawValue( convertValue( value ) );
    }

    @Override
    public final MapGenerator value( final String key, final Object value )
    {
        if ( value instanceof Map )
        {
            this.map( key );
            serializeMap( (Map<?, ?>) value );
            return this.end();
        }

        if ( value instanceof List )
        {
            this.array( key );
            serializeList( (List<?>) value );
            return end();
        }

        if ( value instanceof Multimap )
        {
            this.map( key );
            serializeMultimap( (Multimap<?, ?>) value );
            return this.end();
        }

        return rawValue( key, convertValue( value ) );
    }

    @Override
    public MapGenerator rawValue( final Object value )
    {
        addToArray( convertRaw( value ) );
        return this;
    }

    @Override
    public MapGenerator rawValue( final String key, final Object value )
    {
        putInMap( key, convertRaw( value ) );
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

    private Object convertRaw( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof MapSerializable )
        {
            return convertValue( (MapSerializable) value );
        }

        if ( value instanceof Number )
        {
            return convertNumber( (Number) value );
        }

        return value;
    }

    private Object convertValue( final Object value )
    {
        if ( value == null )
        {
            return null;
        }

        if ( value instanceof MapSerializable )
        {
            return convertValue( (MapSerializable) value );
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

        if ( value instanceof Exception )
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

    private Object convertValue( final MapSerializable value )
    {
        final MapGeneratorBase generator = newGenerator();
        value.serialize( generator );
        return generator.getRoot();
    }

    private MapGeneratorBase serializeMap( final Map<?, ?> map )
    {
        for ( final Map.Entry entry : map.entrySet() )
        {
            this.value( entry.getKey().toString(), entry.getValue() );
        }
        return this;
    }

    private MapGeneratorBase serializeList( final List<?> list )
    {
        for ( final Object item : list )
        {
            this.value( item );
        }
        return this;
    }


    private MapGeneratorBase serializeMultimap( final Multimap<?, ?> multimap )
    {
        final Map<?, ? extends Collection<?>> params = multimap.asMap();
        for ( final Object key : params.keySet() )
        {
            final Collection<?> values = params.get( key );
            if ( values.size() == 1 )
            {
                this.value( key.toString(), values.iterator().next() );
            }
            else
            {
                this.array( key.toString() );
                values.forEach( this::value );
                this.end();
            }
        }
        return this;
    }

    protected abstract MapGeneratorBase newGenerator();
}

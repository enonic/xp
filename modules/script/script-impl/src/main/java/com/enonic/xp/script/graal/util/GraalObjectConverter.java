package com.enonic.xp.script.graal.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.serializer.MapSerializable;

public final class GraalObjectConverter
    implements ObjectConverter
{
    private final JavascriptHelper<?> helper;

    private final Context context;

    public GraalObjectConverter( final JavascriptHelper<?> helper, final Context context )
    {
        this.helper = helper;
        this.context = context;
    }

    @Override
    public Object toJs( final Object value )
    {
        if ( value instanceof MapSerializable )
        {
            return toJs( (MapSerializable) value );
        }

        if ( value instanceof List )
        {
            return toJs( (List) value );
        }

        // A callback result reaches Java as a Map, because a handle converts eagerly rather than
        // let a context-bound value escape. Nashorn hands the guest object straight back, so
        // without this branch the same script sees a plain object on one engine and an opaque
        // host value on the other.
        if ( value instanceof Map )
        {
            return toJs( (Map<?, ?>) value );
        }

        if ( value != null && value.getClass().isArray() && !value.getClass().getComponentType().isPrimitive() )
        {
            return toJs( Arrays.asList( (Object[]) value ) );
        }

        return value;
    }

    @Override
    public Object[] toJsArray( final Object[] values )
    {
        final Object[] result = new Object[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = toJs( values[i] );
        }
        return result;
    }

    private Object toJs( final MapSerializable value )
    {
        final GraalScriptMapGenerator generator = new GraalScriptMapGenerator( this.helper );
        value.serialize( generator );
        return generator.getRoot();
    }

    private Object toJs( final Map<?, ?> map )
    {
        final Object object = this.helper.newJsObject();
        for ( final Map.Entry<?, ?> entry : map.entrySet() )
        {
            GraalJSHelper.addToNativeObject( object, String.valueOf( entry.getKey() ), toJs( entry.getValue() ) );
        }

        return object;
    }

    private Object toJs( final List list )
    {
        final Object array = this.helper.newJsArray();
        for ( final Object element : list )
        {
            GraalJSHelper.addToNativeArray( array, toJs( element ) );
        }

        return array;
    }

    @Override
    public Object fromJs( final Object value )
    {
        return toObject( value );
    }

    private Object toObject( final Object source )
    {
        if ( source instanceof Value )
        {
            return toObject( (Value) source );
        }

        return source;
    }

    private Object toObject( final Value source )
    {
        if ( source.isNull() )
        {
            return null;
        }
        else if ( source.isHostObject() )
        {
            return source.asHostObject();
        }
        else if ( source.hasArrayElements() )
        {
            return toList( source );
        }
        else if ( source.canExecute() )
        {
            return toFunction( source );
        }
        else if ( GraalJSHelper.isDateType( source ) )
        {
            return GraalJSHelper.toDate( source );
        }
        else if ( source.isNumber() )
        {
            return source.as( Number.class );
        }
        else if ( source.isString() )
        {
            return source.asString();
        }
        else if ( source.isBoolean() )
        {
            return source.asBoolean();
        }
        else
        {
            return toMap( source );
        }
    }

    private List<Object> toList( final Value source )
    {
        final List<Object> result = new ArrayList<>();
        for ( int i = 0; i < source.getArraySize(); i++ )
        {
            Object converted = toObject( source.getArrayElement( i ) );
            if ( converted != null )
            {
                result.add( converted );
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> toMap( final Object source )
    {
        if ( source instanceof Value )
        {
            return toMap( (Value) source );
        }
        return new HashMap<>();
    }

    private Map<String, Object> toMap( final Value source )
    {
        Map<String, Object> result = new LinkedHashMap<>();
        // parity with the Nashorn converter: a null-valued key stays in the map — dropping it
        // would make {key: null} indistinguishable from {}
        source.getMemberKeys().forEach( key -> result.put( key, toObject( source.getMember( key ) ) ) );
        return result;
    }

    private Function<Object[], Object> toFunction( final Value source )
    {
        final JsFunctionHandle handle = new JsFunctionHandle( context, source );
        return handle::execute;
    }
}

package com.enonic.xp.script.impl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.graalvm.polyglot.Value;

import com.enonic.xp.script.serializer.MapSerializable;

public final class JsObjectConverter
{
    private final JavascriptHelper helper;

    public JsObjectConverter( final JavascriptHelper helper )
    {
        this.helper = helper;
    }

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

        if ( value != null && value.getClass().isArray() && !value.getClass().getComponentType().isPrimitive() )
        {
            return toJs( Arrays.asList( (Object[]) value ) );
        }

        return value;
    }

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
        final ScriptMapGenerator generator = new ScriptMapGenerator( this.helper );
        value.serialize( generator );
        return generator.getRoot();
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
        else if (source.isHostObject()) {
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
            if ( source.fitsInLong() )
            {
                return source.asLong();
            }
            else
            {
                return source.asDouble();
            }
//            if ( source.fitsInInt() )
//            {
//                return source.asInt();
//            }
//            else if ( source.fitsInLong() )
//            {
//                return source.asLong();
//            }
//            else if ( source.fitsInFloat() )
//            {
//                return source.asFloat();
//            }
//            else if ( source.fitsInDouble() )
//            {
//                return source.asDouble();
//            }
//            else if ( source.fitsInByte() )
//            {
//                return source.asByte();
//            }
//            else
//            {
//                return source.asShort();
//            }
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
        source.getMemberKeys().forEach( key -> {
            Object converted = toObject( source.getMember( key ) );
            if ( converted != null )
            {
                result.put( key, converted );
            }
        } );
        return result;
    }

    private Function<Object[], Object> toFunction( final Value source )
    {
        return arg -> toObject( source.execute( arg ) );
    }
}

package com.enonic.xp.script.serializer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.enonic.xp.util.GenericValue;

public final class ConfigSerializer
{
    public static void serializeDescriptorConfig( final MapGenerator gen, final GenericValue config )
    {
        gen.map( "config" );
        serializeConfig( gen, config );
        gen.end();
    }

    public static void serializeConfig( final MapGenerator gen, final GenericValue config )
    {
        config.getProperties().forEach( e -> {
            final String propertyName = e.getKey();
            if ( e.getValue().getType() == GenericValue.Type.LIST )
            {
                gen.array( propertyName );
                for ( final GenericValue property : e.getValue().asList() )
                {
                    serializeConfigProperty( gen, propertyName, property, true );
                }
                gen.end();
            }
            else
            {
                serializeConfigProperty( gen, propertyName, e.getValue(), false );
            }
        } );
    }

    private static void serializeConfigProperty( final MapGenerator gen, final String propertyName, final GenericValue property,
                                                 final boolean withoutName )
    {
        switch ( property.getType() )
        {
            case STRING -> writeValue( gen, propertyName, property.asString(), withoutName );
            case BOOLEAN -> writeValue( gen, propertyName, property.asBoolean(), withoutName );
            case NUMBER -> writeValue( gen, propertyName, property.asDouble(), withoutName );
            case LIST -> writeArray( gen, propertyName, withoutName,
                                     g -> property.asList().forEach( pv -> g.value( unwrapScalarOrComposite( pv ) ) ) );
            case OBJECT -> writeMap( gen, propertyName, withoutName, g -> property.getProperties()
                .forEach( entry -> g.value( entry.getKey(), unwrapScalarOrComposite( entry.getValue() ) ) ) );
            default -> throw new AssertionError( "Unrecognized property type: " + property );
        }
    }

    private static void writeValue( final MapGenerator gen, final String name, final Object value, final boolean withoutName )
    {
        if ( withoutName )
        {
            gen.value( value );
        }
        else
        {
            gen.value( name, value );
        }
    }

    private static void writeMap( final MapGenerator gen, final String name, final boolean withoutName,
                                  final Consumer<MapGenerator> mapContent )
    {
        if ( withoutName )
        {
            gen.map();
        }
        else
        {
            gen.map( name );
        }
        mapContent.accept( gen );
        gen.end();
    }

    private static void writeArray( final MapGenerator gen, final String name, final boolean withoutName,
                                    final Consumer<MapGenerator> arrayContent )
    {
        if ( withoutName )
        {
            gen.array();
        }
        else
        {
            gen.array( name );
        }
        arrayContent.accept( gen );
        gen.end();
    }

    private static Object unwrapScalarOrComposite( final GenericValue propertyValue )
    {
        return switch ( propertyValue.getType() )
        {
            case STRING -> propertyValue.asString();
            case BOOLEAN -> propertyValue.asBoolean();
            case NUMBER -> propertyValue.asDouble();
            case LIST -> propertyValue.asList().stream().map( ConfigSerializer::unwrapScalarOrComposite ).toList();
            case OBJECT -> propertyValue.getProperties()
                .stream()
                .collect( Collectors.toMap( Map.Entry::getKey, e -> unwrapScalarOrComposite( e.getValue() ), ( a, b ) -> a,
                                            LinkedHashMap::new ) );
        };
    }
}

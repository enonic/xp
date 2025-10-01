package com.enonic.xp.script.serializer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.PropertyValue;

public final class InputTypeConfigSerializer
{
    public static void serializeConfig( final MapGenerator gen, final InputTypeConfig config )
    {
        gen.map( "config" );
        for ( String name : config.getNames() )
        {
            final Set<InputTypeProperty> properties = config.getProperties( name );
            if ( properties.size() > 1 )
            {
                gen.array( name );
                for ( final InputTypeProperty property : properties )
                {
                    serializeConfigProperty( gen, property, true );
                }
                gen.end();
            }
            else
            {
                serializeConfigProperty( gen, properties.iterator().next(), false );
            }
        }
        gen.end();
    }

    private static void serializeConfigProperty( final MapGenerator gen, final InputTypeProperty property, final boolean withoutName )
    {
        final PropertyValue propertyValue = property.getValue();

        switch ( propertyValue.getType() )
        {
            case STRING -> writeValue( gen, property.getName(), propertyValue.asString(), withoutName );
            case BOOLEAN -> writeValue( gen, property.getName(), propertyValue.asBoolean(), withoutName );
            case NUMBER -> writeValue( gen, property.getName(), propertyValue.asDouble(), withoutName );
            case LIST -> writeArray( gen, property.getName(), withoutName,
                                     g -> propertyValue.asList().forEach( pv -> g.value( unwrapScalarOrComposite( pv ) ) ) );
            case OBJECT -> writeMap( gen, property.getName(), withoutName, g -> propertyValue.getProperties()
                .forEach( entry -> g.value( entry.getKey(), unwrapScalarOrComposite( entry.getValue() ) ) ) );
            default -> throw new AssertionError( "Unrecognized property type: " + property.getValue() );
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

    private static Object unwrapScalarOrComposite( final PropertyValue propertyValue )
    {
        return switch ( propertyValue.getType() )
        {
            case STRING -> propertyValue.asString();
            case BOOLEAN -> propertyValue.asBoolean();
            case NUMBER -> propertyValue.asDouble();
            case LIST -> propertyValue.asList().stream().map( InputTypeConfigSerializer::unwrapScalarOrComposite ).toList();
            case OBJECT -> propertyValue.getProperties()
                .stream()
                .collect( Collectors.toMap( Map.Entry::getKey, e -> unwrapScalarOrComposite( e.getValue() ), ( a, b ) -> a,
                                            LinkedHashMap::new ) );
        };
    }
}

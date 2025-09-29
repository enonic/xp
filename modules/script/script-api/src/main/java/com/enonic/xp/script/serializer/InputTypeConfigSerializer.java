package com.enonic.xp.script.serializer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.enonic.xp.inputtype.BooleanPropertyValue;
import com.enonic.xp.inputtype.DoublePropertyValue;
import com.enonic.xp.inputtype.InputTypeConfig;
import com.enonic.xp.inputtype.InputTypeProperty;
import com.enonic.xp.inputtype.IntegerPropertyValue;
import com.enonic.xp.inputtype.ListPropertyValue;
import com.enonic.xp.inputtype.LongPropertyValue;
import com.enonic.xp.inputtype.ObjectPropertyValue;
import com.enonic.xp.inputtype.PropertyValue;
import com.enonic.xp.inputtype.StringPropertyValue;

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

        switch ( propertyValue )
        {
            case StringPropertyValue(String value) -> writeValue( gen, property.getName(), value, withoutName );
            case BooleanPropertyValue(boolean value) -> writeValue( gen, property.getName(), value, withoutName );
            case DoublePropertyValue(double value) -> writeValue( gen, property.getName(), value, withoutName );
            case LongPropertyValue(long value) -> writeValue( gen, property.getName(), value, withoutName );
            case IntegerPropertyValue(int value) -> writeValue( gen, property.getName(), value, withoutName );
            case ListPropertyValue(List<PropertyValue> value) ->
                writeArray( gen, property.getName(), withoutName, g -> value.forEach( pv -> g.value( unwrapScalarOrComposite( pv ) ) ) );
            case ObjectPropertyValue objectPropertyValue -> writeMap( gen, property.getName(), withoutName,
                                                                      g -> objectPropertyValue.getProperties()
                                                                          .forEach( entry -> g.value( entry.getKey(),
                                                                                                      unwrapScalarOrComposite(
                                                                                                          entry.getValue() ) ) ) );
            default -> throw new IllegalArgumentException( "Unrecognized property type: " + property.getValue() );
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
        return switch ( propertyValue )
        {
            case StringPropertyValue spv -> spv.value();
            case BooleanPropertyValue bpv -> bpv.value();
            case IntegerPropertyValue ipv -> ipv.value();
            case DoublePropertyValue dpv -> dpv.value();
            case LongPropertyValue lpv -> lpv.value();
            case ListPropertyValue lpv -> lpv.value().stream().map( InputTypeConfigSerializer::unwrapScalarOrComposite ).toList();
            case ObjectPropertyValue opv -> opv.getProperties()
                .stream()
                .collect( Collectors.toMap( Map.Entry::getKey, e -> unwrapScalarOrComposite( e.getValue() ), ( a, b ) -> a,
                                            LinkedHashMap::new ) );
        };
    }
}

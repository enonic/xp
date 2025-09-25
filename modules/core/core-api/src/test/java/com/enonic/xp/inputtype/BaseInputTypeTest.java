package com.enonic.xp.inputtype;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Input;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Reference;

public abstract class BaseInputTypeTest
{
    protected final InputType type;

    public BaseInputTypeTest( final InputType type )
    {
        this.type = type;
    }

    protected final Property booleanProperty( final boolean value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addBoolean( "test", value );
    }

    protected final Property stringProperty( final String value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addString( "test", value );
    }

    protected final Property referenceProperty( final String value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addReference( "test", Reference.from( value ) );
    }

    protected final Property dateTimeProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addInstant( "test", Instant.MAX );
    }

    protected final Property localDateTimeProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addLocalDateTime( "test", LocalDateTime.MAX );
    }

    protected final Property localDateProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addLocalDate( "test", LocalDate.MAX );
    }

    protected final Property localTimeProperty()
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addLocalTime( "test", LocalTime.MAX );
    }

    protected final Property doubleProperty( final double value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addDouble( "test", value );
    }

    protected final Property geoPointProperty( final String value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addGeoPoint( "test", GeoPoint.from( value ) );
    }

    protected final Property longProperty( final long value )
    {
        final PropertyTree tree = new PropertyTree();
        return tree.addLong( "test", value );
    }

    protected final Input.Builder getDefaultInputBuilder( final InputTypeName inputTypeName, final String defaultValue )
    {
        final InputTypeProperty defaultProperty = InputTypeProperty.create( "default", new StringPropertyValue( defaultValue ) ).build();

        final InputTypeDefault inputTypeDefault = InputTypeDefault.create().property( defaultProperty ).build();

        return Input.create().name( "inputName" ).label( "label" ).inputType( inputTypeName ).defaultValue( inputTypeDefault );

    }
}
